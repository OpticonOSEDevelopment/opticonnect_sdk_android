# OptiConnect SDK

OptiConnect SDK enables seamless integration with [Opticon](https://opticon.com/)'s BLE [OPN-2500](https://opticon.com/product/opn-2500/) and [OPN-6000](https://opticon.com/product/opn-6000/) barcode scanners. This SDK allows you to manage Bluetooth Low Energy (BLE) connections, handle scanner data streams, and programmatically control scanner settings via commands.

**Note:** This package is independent of the [OptiConnect application](https://opticon.com/opticonnect/), which provides additional device management features. The SDK provides lower-level BLE functionality directly interacting with Opticon's BLE devices.

## Features

-  Bluetooth discovery and connection management for OPN-2500 and OPN-6000 BLE scanners.
-  Real-time data streaming, including barcode data reception and BLE device state monitoring.
-  Programmatic control of scanner settings (e.g., scan modes, illumination, connection pooling, etc.).
-  Exclusive connection management: Ensure stable device pairing in multi-device environments by assigning unique connection pool IDs, preventing previously paired devices from hijacking active connections.
-  Command management and customization for BLE services and scanner configurations.

## Getting Started


### Prerequisites

-  Opticon BLE [OPN-2500](https://opticon.com/product/opn-2500/) or [OPN-6000](https://opticon.com/product/opn-6000/) barcode scanner(s).

| ![OPN-2500](https://raw.githubusercontent.com/opticonosedevelopment/opticonnect_sdk_flutter/main/assets/images/OPN-2500.png) | ![OPN-6000](https://raw.githubusercontent.com/opticonosedevelopment/opticonnect_sdk_flutter/main/assets/images/OPN-6000.png) |
| :--------------------------------------------------------------------------------------------------------------------------: | :--------------------------------------------------------------------------------------------------------------------------: |
|                                                         **OPN-2500**                                                         |                                                         **OPN-6000**                                                         |

### Setup

to enable Bluetooth discovery and connection on Android, add the following permissions to your AndroidManifest.xml file located at android/app/src/main/AndroidManifest.xml below the manifest entry at the top:

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

### Building the .aar Library

To build the .aar file for the OptiConnect SDK with shadowed dependencies, follow these steps:

1. Run the shadowJar Task: ./gradlew shadowJar
2. Package the Final .aar: ./gradlew bundleShadowedReleaseAar

The generated .aar file will be located in build/outputs/shadowed-aar/.

## Including OptiConnect SDK in Your Project

### 1. Add the `.aar` File to Your Project

1. Download the latest `.aar` file (`opticonnectsdk-release.aar`) from [Opticon's website](https://opticon.com/) or a direct link provided by your team.
2. Place the `.aar` file in your project’s `libs` directory (e.g., `app/libs/opticonnectsdk-release.aar`).

### 2. Update Your `build.gradle.kts`

Add the `.aar` file and required dependencies in your `build.gradle.kts` file under `dependencies`:

```kotlin
dependencies {
    // Include the .aar file
    implementation(files("libs/opticonnectsdk-release.aar"))

    // Required dependencies
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.rx3)
    implementation(libs.rxandroidble)
    implementation(libs.rxkotlin)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Optional dependencies for testing and debugging
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.espresso.core)
    testImplementation(libs.junit)
}
