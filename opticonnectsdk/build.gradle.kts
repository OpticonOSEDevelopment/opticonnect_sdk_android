plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.shadow)
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
    implementation(libs.dagger)
    ksp(libs.dagger.compiler)
    implementation(libs.timber)
    implementation(libs.rxandroidble)
    implementation(libs.coroutines)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.rx3)
    implementation(libs.mockk)
    implementation(libs.rxkotlin)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.coroutines.test)
}

// Create a custom resolvable configuration for shadowing
val shadowRuntimeClasspath by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(configurations["implementation"], configurations["runtimeOnly"])
}

// Register the ShadowJar task manually
// Register the ShadowJar task manually
tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("all")

    dependsOn("compileReleaseKotlin")
    dependsOn("compileReleaseJavaWithJavac")

    val kotlinClassesDir = file("build/tmp/kotlin-classes/release") // Path where your classes are located
    val daggerGeneratedDir = file("build/intermediates/javac/release/compileReleaseJavaWithJavac/classes")

    if (daggerGeneratedDir.exists()) {
        from(daggerGeneratedDir)
    }

    // Check if the directory exists before including it
    if (kotlinClassesDir.exists()) {
        from(kotlinClassesDir)
    }

    // Include the compiled output of the project
    from("build/classes/kotlin/main") // Include compiled Kotlin classes
    from(android.sourceSets["main"].resources.srcDirs)
    from("build/generated/ksp/main/kotlin") // Include KSP-generated Kotlin files

    // Use the custom configuration
    configurations = listOf(
        project.configurations.getByName("shadowRuntimeClasspath"),
        project.configurations.getByName("releaseRuntimeClasspath"),
        project.configurations.getByName("releaseCompileClasspath")
    )

    relocate("dagger", "com.opticon.opticonnect.dagger")
    relocate("javax.inject", "com.opticon.opticonnect.javax.inject")
    relocate("jakarta", "com.opticon.opticonnect.jakarta")
    relocate("org.jetbrains", "com.opticon.opticonnect.org.jetbrains")

    // Relocate dependencies to avoid conflicts with the client
//    relocate("kotlinx", "com.opticon.opticonnect.kotlinx")
//    relocate("org.koin", "com.opticon.opticonnect.koin")
//    relocate("org.jetbrains", "com.opticon.opticonnect.jetbrains")

    exclude("_COROUTINE/**")
    exclude("win32-x86/**")
    exclude("win32-x86-64/**")
    exclude("bleshadow/**") // Exclude the bleshadow package
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("androidx/**")
    exclude("com/google/**")
    exclude("com/jakewharton/**")
    exclude("io/**")
    exclude("junit/**")
    exclude("net/**")
    exclude("org/hamcrest/**")
    exclude("org/intellij/**")
    exclude("org/junit/**")
    exclude("org/objenesis/**")
    exclude("org/reactivestreams/**")
    exclude("**/*.aar")

    exclude("META-INF/kotlin/**")
    exclude("META-INF/maven/**")
    exclude("META-INF/proguard/**")
    exclude("META-INF/versions/**")
    exclude("META-INF/*.kotlin_module")
    exclude("META-INF/INDEX.LIST")
    exclude("META-INF/annotation.kotlin_module")
    exclude("META-INF/descriptors.*")
    exclude("META-INF/concurrent-futures-ktx.kotlin_module")
    exclude("META-INF/licenses/**")
    exclude("META-INF/com.android.tools/**")
    exclude("META-INF/NOTICE")
    exclude("META-INF/LICENSE")
    exclude("META-INF/manifest.mf")
    exclude("META-INF/notice")

    // Merge META-INF service files to avoid conflicts
    mergeServiceFiles {
        include("META-INF/services/**")
        include("META-INF/spring.handlers")
        include("META-INF/spring.schemas")
        include("META-INF/spring.factories")
    }
}

val createClassesJar = tasks.register<Jar>("createClassesJar") {
    group = "build"
    description = "Create a classes.jar file with shadowed classes"

    dependsOn("shadowJar") // Explicitly depends on shadowJar

    val shadowJarTask = tasks.named("shadowJar", com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar::class.java).get()
    val shadowJarFile = shadowJarTask.outputs.files.singleFile

    // Extract classes from the shadowed JAR
    from(zipTree(shadowJarFile)) {
        include("**/*.class")
    }

    archiveBaseName.set("classes")
    destinationDirectory.set(layout.buildDirectory.dir("tmp/classes"))
}

tasks.register<Zip>("bundleShadowedReleaseAar") {
    group = "build"
    description = "Bundle shadowed dependencies with AAR"

    dependsOn("assembleRelease", "createClassesJar")

    val classesJarProvider = createClassesJar.flatMap { it.archiveFile }
    val aarFile = layout.buildDirectory.file("outputs/aar/${project.name}-release.aar")

    // Unzip the original AAR, excluding the original classes.jar
    from(zipTree(aarFile)) {
        exclude("classes.jar") // Exclude the original classes.jar from the AAR
    }

    // Add the newly created classes.jar to the root of the AAR
    from(classesJarProvider) {
        into("/") // Place the new classes.jar at the root of the AAR
    }

    archiveFileName.set("${project.name}-shadowed-release.aar")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/shadowed-aar"))
}
