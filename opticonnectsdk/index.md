# Module opticonnect sdk

OptiConnect SDK enables seamless integration with [Opticon](https://opticon.com/)'s BLE [OPN-2500](https://opticon.com/product/opn-2500/) and [OPN-6000](https://opticon.com/product/opn-6000/) barcode scanners. This SDK allows you to manage Bluetooth Low Energy (BLE) connections, handle scanner data streams, and programmatically control scanner settings via commands.

## Features

- Bluetooth discovery and connection management for OPN-2500 and OPN-6000 BLE scanners.
- Real-time data streaming, including barcode data reception and BLE device state monitoring.
- Programmatic control of scanner settings (e.g., scan modes, illumination, connection pooling, etc.).
- Exclusive connection management: Ensure stable device pairing in multi-device environments by assigning unique connection pool IDs, preventing previously paired devices from hijacking active connections.
- Command management and customization for BLE services and scanner configurations.

## Getting Started

### 1. Prerequisites

-  Opticon BLE [OPN-2500](https://opticon.com/product/opn-2500/) or [OPN-6000](https://opticon.com/product/opn-6000/) barcode scanner(s).

<table style="width: 100%; text-align: center; table-layout: fixed;">
    <tr>
        <td style="width: 50%; border: 1px solid #ddd; border-radius: 8px; padding: 10px; box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1); vertical-align: middle;">
            <div style="display: flex; flex-direction: column; align-items: center; height: 200px; position: relative;">
                <div style="flex-grow: 1; display: flex; align-items: center; justify-content: center;">
                    <img src="images/OPN-2500.png" alt="OPN-2500" style="max-width: 150px; height: auto;">
                </div>
                <div style="position: absolute; bottom: 0px; font-weight: bold;">
                    OPN-2500
                </div>
            </div>
        </td>
        <td style="width: 50%; border: 1px solid #ddd; border-radius: 8px; padding: 10px; box-shadow: 0px 2px 8px rgba(0, 0, 0, 0.1); vertical-align: middle;">
            <div style="display: flex; flex-direction: column; align-items: center; height: 200px; position: relative;">
                <div style="flex-grow: 1; display: flex; align-items: center; justify-content: center;">
                    <img src="images/OPN-6000.png" alt="OPN-6000" style="max-width: 150px; height: auto;">
                </div>
                <div style="position: absolute; bottom: 0px; font-weight: bold;">
                    OPN-6000
                </div>
            </div>
        </td>
    </tr>
</table>

### 2. Building the .aar library

To build the `.aar` file for the OptiConnect SDK with shadowed dependencies, follow these steps:

1. Run the shadowJar task: `./gradlew shadowJar`
2. Package the final `.aar`: `./gradlew bundleShadowedReleaseAar`

The generated `.aar` file will be located in `build/outputs/aar/`.

### 3. Adding the `.aar` library to your project

1. Download or build the `.aar` file (`opticonnectsdk.aar`) as outlined in the previous section.
2. Place the `.aar` file in your project’s `libs` directory (e.g., `app/libs/opticonnectsdk.aar`).

### 4. Updating your `build.gradle(.kts)`

Add the `.aar` file and required dependencies in your `build.gradle(.kts)` file under `dependencies`:

```kotlin
dependencies {
    // Include the .aar file
    implementation(files("libs/opticonnectsdk.aar"))

    // Core Android and Kotlin dependencies
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Coroutines dependencies
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.rx3)

    // RxAndroidBLE and RxKotlin for BLE and reactive programming
    implementation(libs.rxandroidble)
    implementation(libs.rxkotlin)
}
```

### 4. Android Manifest Bluetooth Permissions

To enable Bluetooth discovery and connection on Android, add the following permissions to your AndroidManifest.xml file located at android/app/src/main/AndroidManifest.xml below the manifest entry:

```xml
<uses-feature android:name="android.hardware.bluetooth_le" android:required="false" />

<!-- New Bluetooth permissions for Android 12 or higher -->
<uses-permission android:name="android.permission.BLUETOOTH_SCAN"/>
<uses-permission android:name="android.permission.BLUETOOTH_CONNECT" />
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />

<!-- Legacy permissions for Android 11 or lower -->
<uses-permission android:name="android.permission.BLUETOOTH" android:maxSdkVersion="30" />
<uses-permission android:name="android.permission.BLUETOOTH_ADMIN" android:maxSdkVersion="30" />

<!-- Legacy permission for Android 9 or lower -->
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" android:maxSdkVersion="28" />
```
