<h1>
  OptiConnect SDK
  <img align="right" src="opticonnectsdk/images/opticon_logo.svg" alt="Opticon" width="200">
</h1>

OptiConnect SDK enables seamless integration with [Opticon](https://opticon.com/)'s BLE [OPN-2500](https://opticon.com/product/opn-2500/) and [OPN-6000](https://opticon.com/product/opn-6000/) barcode scanners. This SDK allows you to manage Bluetooth Low Energy (BLE) connections, handle scanner data streams, and programmatically control scanner settings via commands.

## [OptiConnect SDK Documentation](https://opticonosedevelopment.github.io/opticonnect_sdk_android/)

For full setup details, usage notes, and API reference, visit the [OptiConnect SDK Documentation](https://opticonosedevelopment.github.io/opticonnect_sdk_android/).

## Features

- Bluetooth discovery and connection management for OPN-2500 and OPN-6000 BLE scanners.
- Real-time data streaming, including barcode data reception and BLE device state monitoring.
- Programmatic control of scanner settings (e.g., scan modes, illumination, connection pooling, etc.).
- Exclusive connection management: Ensure stable device pairing in multi-device environments by assigning unique connection pool IDs, preventing previously paired devices from hijacking active connections.
- Command management and customization for BLE services and scanner configurations.

## Quick Start

This is the shortest path from adding the SDK to receiving barcode data:

1. Add the AAR.
2. Add the required dependencies.
3. Add Bluetooth permissions.
4. Request runtime permissions in your app.
5. Initialize `OptiConnect`.
6. Start discovery.
7. Connect to a scanner.
8. Listen for barcode data.

```kotlin
OptiConnect.initialize(context)
OptiConnect.bluetoothManager.startDiscovery()

lifecycleScope.launch {
    val device = OptiConnect.bluetoothManager.listenToDiscoveredDevices().first()

    OptiConnect.bluetoothManager.connect(device.deviceId)

    OptiConnect.bluetoothManager.listenToBarcodeData(device.deviceId).collect { barcode ->
        Log.d("OptiConnect", barcode.data)
    }
}
```

For production code, handle runtime permissions, connection state, disconnects, and coroutine lifecycle cancellation. See the complete examples below.

## Installation

### 1. Requirements

- One supported scanner: [OPN-2500](https://opticon.com/product/opn-2500/) or [OPN-6000](https://opticon.com/product/opn-6000/)
- Android minimum SDK: 26
- Compile SDK: 36
- Android Build Tools: 36.0.0
- JDK for building: 17
- Gradle Wrapper: 8.13
- Kotlin/AGP: use the versions pinned in `gradle/libs.versions.toml`

### 2. Add the AAR

Copy [`libs/opticonnectsdk.aar`](libs/opticonnectsdk.aar) into your app module's `libs` directory.

The Kotlin and Java sample apps already include the AAR:

- [`examples/kotlin/app/libs/opticonnectsdk.aar`](examples/kotlin/app/libs/opticonnectsdk.aar)
- [`examples/java/app/libs/opticonnectsdk.aar`](examples/java/app/libs/opticonnectsdk.aar)

### 3. Add Dependencies

Add the AAR and required external dependencies to your app module `build.gradle(.kts)`:

```kotlin
dependencies {
    implementation(files("libs/opticonnectsdk.aar"))

    implementation("androidx.core:core:1.17.0")
    implementation("com.polidea.rxandroidble3:rxandroidble:1.19.1")
    implementation("io.reactivex.rxjava3:rxkotlin:3.0.1")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.11.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-rx3:1.11.0")
}
```

Java projects can use the callback APIs, but still need the Kotlin plugin and coroutine dependencies because the SDK is Kotlin-based internally. Long-running callback listeners return a `ListenerSubscription`; call `close()` when the screen or generated workflow no longer needs updates.

### 4. Add Bluetooth Permissions

Add these permissions to `AndroidManifest.xml`:

```xml
<uses-feature android:name="android.hardware.bluetooth_le" android:required="false" />

<uses-permission
    android:name="android.permission.BLUETOOTH_SCAN"
    android:usesPermissionFlags="neverForLocation" />
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />

<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" android:maxSdkVersion="28" />
```

The host app must request runtime permissions before discovery or connection:

- Android 12+ / API 31+: `BLUETOOTH_SCAN` and `BLUETOOTH_CONNECT`
- Android 11 and lower: `ACCESS_FINE_LOCATION`

## Common Usage

### Discover Devices

```kotlin
OptiConnect.bluetoothManager.startDiscovery()

lifecycleScope.launch {
    OptiConnect.bluetoothManager.listenToDiscoveredDevices().collect { device ->
        Log.d("OptiConnect", "Found device: ${device.deviceId}")
    }
}
```

### Connect To A Scanner

```kotlin
lifecycleScope.launch {
    try {
        OptiConnect.bluetoothManager.connect(deviceId)
    } catch (e: Exception) {
        Log.e("OptiConnect", "Failed to connect", e)
    }
}
```

### Listen To Barcode Data

```kotlin
lifecycleScope.launch {
    OptiConnect.bluetoothManager.listenToBarcodeData(deviceId).collect { barcode ->
        Log.d("OptiConnect", barcode.data)
    }
}
```

Java callback listeners should keep the returned subscription and close it during cleanup:

```text
ListenerSubscription barcodeSubscription =
    OptiConnect.INSTANCE.getBluetoothManager().listenToBarcodeData(deviceId, callback);

barcodeSubscription.close();
```

### Listen To Connection State

```kotlin
lifecycleScope.launch {
    OptiConnect.bluetoothManager.listenToConnectionState(deviceId).collect { state ->
        Log.d("OptiConnect", "Device $deviceId state: $state")
    }
}
```

### Listen To Battery Data

```kotlin
lifecycleScope.launch {
    OptiConnect.bluetoothManager.listenToBatteryPercentage(deviceId).collect { battery ->
        Log.d("OptiConnect", "Battery: $battery%")
    }
}

lifecycleScope.launch {
    OptiConnect.bluetoothManager.listenToBatteryStatus(deviceId).collect { status ->
        Log.d("OptiConnect", "Charging: ${status.isCharging}")
    }
}
```

### Disconnect And Close

```kotlin
lifecycleScope.launch {
    OptiConnect.bluetoothManager.disconnect(deviceId)
}

override fun onDestroy() {
    super.onDestroy()
    OptiConnect.close()
}
```

## Examples

Complete working apps are available in:

- [Kotlin example app](examples/kotlin)
- [Advanced Kotlin example app](examples/kotlin-advanced)
- [Java example app](examples/java)
- [Kotlin MainActivity.kt](examples/kotlin/app/src/main/kotlin/com/opticon/opticonnect_sdk_example/MainActivity.kt)
- [AdvancedScannerActivity.kt](examples/kotlin-advanced/app/src/main/kotlin/com/opticon/opticonnect_sdk_example/AdvancedScannerActivity.kt)
- [Java MainActivity.java](examples/java/app/src/main/java/com/example/opticonnect_sdk_example_java/MainActivity.java)
- [Java activity_main.xml](examples/java/app/src/main/res/layout/activity_main.xml)

The simple Kotlin and Java examples show the basic BLE flow. The advanced Kotlin example shows a manual scanner list with per-device connect/disconnect controls, live barcode and battery fields, and a small scanner setting toggle.

## Build The SDK AAR

This step is only needed when building the SDK from source.

```bash
./gradlew :opticonnectsdk:bundleShadowedReleaseAar
```

On Windows:

```powershell
.\gradlew.bat :opticonnectsdk:bundleShadowedReleaseAar
```

The generated AAR is written to `opticonnectsdk/build/outputs/aar/opticonnectsdk.aar`.

The shaded AAR relocates SDK-internal dependencies such as Room, Dagger, SQLite, and Timber to reduce conflicts with the host app. External runtime/API dependencies such as Kotlin, coroutines, RxAndroidBLE, and RxKotlin remain normal app dependencies so the host app can control those versions.
