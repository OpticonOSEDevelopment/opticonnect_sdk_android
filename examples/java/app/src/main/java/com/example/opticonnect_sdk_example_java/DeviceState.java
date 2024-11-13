package com.example.opticonnect_sdk_example_java;

import com.opticon.opticonnect.sdk.api.enums.BleDeviceConnectionState;

public class DeviceState {
    private String connectedDeviceId = "";
    private BleDeviceConnectionState connectionState = BleDeviceConnectionState.DISCONNECTED;
    private String barcodeData = null;
    private Integer batteryPercentage = null;
    private Boolean isCharging = null;

    // Simplified getters and setters
    public String getConnectedDeviceId() { return connectedDeviceId; }
    public void setConnectedDeviceId(String id) { this.connectedDeviceId = id; }

    public BleDeviceConnectionState getConnectionState() { return connectionState; }
    public void setConnectionState(BleDeviceConnectionState state) { this.connectionState = state; }

    public String getBarcodeData() { return barcodeData; }
    public void setBarcodeData(String data) { this.barcodeData = data; }

    public Integer getBatteryPercentage() { return batteryPercentage; }
    public void setBatteryPercentage(Integer percentage) { this.batteryPercentage = percentage; }

    public Boolean getIsCharging() { return isCharging; }
    public void setIsCharging(Boolean isCharging) { this.isCharging = isCharging; }
}