plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
}

android {
    namespace = "com.example.opticonnect.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    sourceSets["main"].assets.srcDirs("src/main/assets")
    sourceSets["main"].java.srcDirs("build/generated/ksp/main/kotlin")

    sourceSets["androidTest"].java.srcDirs("src/androidTest/java")
}

dependencies {
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    implementation(libs.junit)
    implementation(libs.androidx.runner)
    implementation(libs.androidx.core)
    ksp(libs.room.compiler)
    implementation(libs.koin)
    implementation(libs.koin.core)
    implementation(libs.koin.annotations)
    implementation(libs.koin.test)
    ksp(libs.koin.compiler)
    implementation(libs.timber)
    implementation(libs.rxandroidble)
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
    implementation(libs.mockk)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.coroutines.test)
}