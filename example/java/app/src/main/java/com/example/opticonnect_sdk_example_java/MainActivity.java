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

import kotlin.Unit;

public class MainActivity extends ComponentActivity {

    private DeviceState deviceState = new DeviceState();
    private TextView connectionStatusText, barcodeDataText, batteryPercentageText, chargingStatusText;

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
        OptiConnect.INSTANCE.initialize(this);
        OptiConnect.INSTANCE.getBluetoothManager().startDiscovery();

        OptiConnect.INSTANCE.getBluetoothManager().listenToDiscoveredDevices(new Callback<>() {
            @Override
            public void onSuccess(BleDiscoveredDevice device) {
                if (deviceState.getConnectionState() == BleDeviceConnectionState.DISCONNECTED) {
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
            }
        });
    }

    private void startListeningToDeviceData(String deviceId) {
        OptiConnect.INSTANCE.getBluetoothManager().listenToBarcodeData(deviceId, new Callback<>() {
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

        OptiConnect.INSTANCE.getBluetoothManager().listenToBatteryPercentage(deviceId, new Callback<>() {
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

        OptiConnect.INSTANCE.getBluetoothManager().listenToBatteryStatus(deviceId, new Callback<>() {
            @Override
            public void onSuccess(BatteryLevelStatus status) {
                deviceState.setIsCharging(status.isCharging());
                updateUI();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("OptiConnect", "Error receiving battery status: " + error.getMessage());
            }
        });
    }

    private void listenToConnectionState(String deviceId) {
        OptiConnect.INSTANCE.getBluetoothManager().listenToConnectionState(deviceId, new Callback<>() {
            @Override
            public void onSuccess(BleDeviceConnectionState state) {
                deviceState.setConnectionState(state);
                updateUI();
            }

            @Override
            public void onError(@NonNull Throwable error) {
                Log.e("OptiConnect", "Error receiving connection state: " + error.getMessage());
            }
        });
    }

    private void disconnectDevice() {
        // Reset DeviceState and update UI
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
        chargingStatusText.setText("Charging: " + (deviceState.getIsCharging() != null ? (deviceState.getIsCharging() ? "Yes" : "No") : "Unknown"));
    }
}
