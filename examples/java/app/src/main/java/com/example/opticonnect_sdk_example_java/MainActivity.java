package com.example.opticonnect_sdk_example_java;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.ComponentActivity;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.opticon.opticonnect.sdk.api.OptiConnect;
import com.opticon.opticonnect.sdk.api.entities.BarcodeData;
import com.opticon.opticonnect.sdk.api.entities.BatteryLevelStatus;
import com.opticon.opticonnect.sdk.api.entities.BleDiscoveredDevice;
import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState;
import com.opticon.opticonnect.sdk.api.interfaces.Callback;
import com.opticon.opticonnect.sdk.api.interfaces.ListenerSubscription;

import kotlin.Unit;

public class MainActivity extends ComponentActivity {

    private DeviceState deviceState = new DeviceState();
    private TextView connectionStatusText, barcodeDataText, batteryPercentageText, chargingStatusText;
    private boolean userRequestedDisconnect = false;
    private ListenerSubscription discoverySubscription;
    private ListenerSubscription connectionStateSubscription;
    private ListenerSubscription barcodeDataSubscription;
    private ListenerSubscription batteryPercentageSubscription;
    private ListenerSubscription batteryStatusSubscription;

    private final ActivityResultLauncher<String[]> requestPermissionsLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), this::onPermissionsResult);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Bind UI elements
        connectionStatusText = findViewById(R.id.connectionStatusText);
        barcodeDataText = findViewById(R.id.barcodeDataText);
        batteryPercentageText = findViewById(R.id.batteryPercentageText);
        chargingStatusText = findViewById(R.id.chargingStatusText);
        Button disconnectButton = findViewById(R.id.disconnectButton);

        disconnectButton.setOnClickListener(view -> disconnectDevice());

        checkBluetoothPermissions();
    }

    private void checkBluetoothPermissions() {
        String[] permissions = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S
                ? new String[]{Manifest.permission.BLUETOOTH_SCAN, Manifest.permission.BLUETOOTH_CONNECT}
                : new String[]{Manifest.permission.ACCESS_FINE_LOCATION};

        boolean permissionsGranted = true;
        for (String permission : permissions) {
            permissionsGranted &= ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
        }

        if (permissionsGranted) {
            initializeOptiConnectAndStartDiscovery();
        } else {
            requestPermissionsLauncher.launch(permissions);
        }
    }

    private void onPermissionsResult(@NonNull java.util.Map<String, Boolean> permissions) {
        if (permissions.containsValue(Boolean.FALSE)) {
            Toast.makeText(this, "Bluetooth permissions are required.", Toast.LENGTH_LONG).show();
        } else {
            initializeOptiConnectAndStartDiscovery();
        }
    }

    private void initializeOptiConnectAndStartDiscovery() {
        userRequestedDisconnect = false;
        OptiConnect.INSTANCE.initialize(this);
        OptiConnect.INSTANCE.getBluetoothManager().startDiscovery();

        closeDiscoverySubscription();
        discoverySubscription = OptiConnect.INSTANCE.getBluetoothManager().listenToDiscoveredDevices(new Callback<>() {
            @Override
            public void onSuccess(BleDiscoveredDevice device) {
                if (!userRequestedDisconnect && deviceState.getConnectionState() == BleDeviceConnectionState.DISCONNECTED) {
                    deviceState.setConnectedDeviceId(device.getDeviceId());
                    deviceState.setConnectionState(BleDeviceConnectionState.CONNECTING);
                    updateUI();
                    connectToDevice(device.getDeviceId());
                }
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("OptiConnect", "Error discovering devices: " + error.getMessage());
            }
        });
    }

    private void connectToDevice(String deviceId) {
        OptiConnect.INSTANCE.getBluetoothManager().connect(deviceId, new Callback<>() {
            @Override
            public void onSuccess(Unit result) {
                deviceState.setConnectionState(BleDeviceConnectionState.CONNECTED);
                updateUI();
                startListeningToDeviceData(deviceId);
                listenToConnectionState(deviceId);
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Toast.makeText(MainActivity.this, "Failed to connect: " + error.getMessage(), Toast.LENGTH_SHORT).show();
                deviceState = new DeviceState();
                updateUI();
            }
        });
    }

    private void startListeningToDeviceData(String deviceId) {
        closeDeviceSubscriptions();

        barcodeDataSubscription = OptiConnect.INSTANCE.getBluetoothManager().listenToBarcodeData(deviceId, new Callback<>() {
            @Override
            public void onSuccess(BarcodeData barcode) {
                deviceState.setBarcodeData(barcode.getData());
                updateUI();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("OptiConnect", "Error receiving barcode data: " + error.getMessage());
            }
        });

        batteryPercentageSubscription = OptiConnect.INSTANCE.getBluetoothManager().listenToBatteryPercentage(deviceId, new Callback<>() {
            @Override
            public void onSuccess(Integer batteryPercentage) {
                deviceState.setBatteryPercentage(batteryPercentage);
                updateUI();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("OptiConnect", "Error receiving battery percentage: " + error.getMessage());
            }
        });

        batteryStatusSubscription = OptiConnect.INSTANCE.getBluetoothManager().listenToBatteryStatus(deviceId, new Callback<>() {
            @Override
            public void onSuccess(BatteryLevelStatus status) {
                boolean isPoweredOrCharging = status.isCharging() || status.isWiredCharging() || status.isWirelessCharging();
                Log.d("OptiConnect", "Battery status: " + status);
                deviceState.setIsCharging(isPoweredOrCharging);
                updateUI();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("OptiConnect", "Error receiving battery status: " + error.getMessage());
            }
        });
    }

    private void listenToConnectionState(String deviceId) {
        closeConnectionStateSubscription();
        connectionStateSubscription = OptiConnect.INSTANCE.getBluetoothManager().listenToConnectionState(deviceId, new Callback<>() {
            @Override
            public void onSuccess(BleDeviceConnectionState state) {
                deviceState.setConnectionState(state);
                if (state == BleDeviceConnectionState.DISCONNECTED) {
                    closeDeviceSubscriptions();
                    deviceState = new DeviceState();
                }
                updateUI();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("OptiConnect", "Error receiving connection state: " + error.getMessage());
            }
        });
    }

    private void closeSubscription(ListenerSubscription subscription) {
        if (subscription != null && !subscription.isClosed()) {
            subscription.close();
        }
    }

    private void closeDiscoverySubscription() {
        closeSubscription(discoverySubscription);
        discoverySubscription = null;
    }

    private void closeConnectionStateSubscription() {
        closeSubscription(connectionStateSubscription);
        connectionStateSubscription = null;
    }

    private void closeDeviceDataSubscriptions() {
        closeSubscription(barcodeDataSubscription);
        closeSubscription(batteryPercentageSubscription);
        closeSubscription(batteryStatusSubscription);
        barcodeDataSubscription = null;
        batteryPercentageSubscription = null;
        batteryStatusSubscription = null;
    }

    private void closeDeviceSubscriptions() {
        closeConnectionStateSubscription();
        closeDeviceDataSubscriptions();
    }

    private void disconnectDevice() {
        String deviceId = deviceState.getConnectedDeviceId();
        userRequestedDisconnect = true;
        closeDiscoverySubscription();
        closeDeviceSubscriptions();
        OptiConnect.INSTANCE.getBluetoothManager().stopDiscovery();
        if (deviceId != null && !deviceId.isEmpty()) {
            OptiConnect.INSTANCE.getBluetoothManager().disconnect(deviceId);
        }
        deviceState = new DeviceState();
        updateUI();
    }

    private void updateUI() {
        connectionStatusText.setText("Status: " + deviceState.getConnectionState().name());

        // Set color based on connection state
        switch (deviceState.getConnectionState()) {
            case CONNECTING:
                connectionStatusText.setTextColor(ContextCompat.getColor(this, R.color.connecting_color));
                break;
            case CONNECTED:
                connectionStatusText.setTextColor(ContextCompat.getColor(this, R.color.connected_color));
                break;
            case DISCONNECTED:
                connectionStatusText.setTextColor(ContextCompat.getColor(this, R.color.disconnected_color));
                break;
            default:
                connectionStatusText.setTextColor(ContextCompat.getColor(this, R.color.text_primary));
        }

        barcodeDataText.setText("Barcode Data: " + (deviceState.getBarcodeData() != null ? deviceState.getBarcodeData() : "None"));
        batteryPercentageText.setText("Battery: " + (deviceState.getBatteryPercentage() != null ? deviceState.getBatteryPercentage() + "%" : "N/A"));
        chargingStatusText.setText("Charging/USB power: " + (deviceState.getIsCharging() != null ? (deviceState.getIsCharging() ? "Yes" : "No") : "Unknown"));
    }

    @Override
    protected void onDestroy() {
        closeDiscoverySubscription();
        closeDeviceSubscriptions();
        OptiConnect.INSTANCE.close();
        super.onDestroy();
    }
}
