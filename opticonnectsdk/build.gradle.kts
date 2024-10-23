import org.gradle.kotlin.dsl.java

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.shadow)
    alias(libs.plugins.dokka)
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
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
    implementation(libs.android.documentation)

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
tasks.register<com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar>("shadowJar") {
    archiveClassifier.set("all")

    dependsOn("compileReleaseKotlin", "compileReleaseJavaWithJavac", "mergeReleaseResources")


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
    from("build/intermediates/javac/release/compileReleaseJavaWithJavac/classes")
    from("build/tmp/kotlin-classes/release")
    from("build/classes/kotlin/release")
    from(android.sourceSets["main"].resources.srcDirs)

    // Use the custom configuration
    configurations = listOf(
        project.configurations.getByName("shadowRuntimeClasspath"),
        project.configurations.getByName("releaseRuntimeClasspath"),
        project.configurations.getByName("releaseCompileClasspath")
    )

    val gradleCacheDir = file("${System.getProperty("user.home")}/.gradle/caches/modules-2/files-2.1")
    val timberAarFile = gradleCacheDir.walkTopDown()
        .filter { it.name.startsWith("timber-") && it.name.endsWith(".aar") }
        .firstOrNull()

    val roomAarFile = gradleCacheDir.walkTopDown()
        .filter { it.name.startsWith("room-runtime-") && it.name.endsWith(".aar") }
        .firstOrNull()

    val sqliteAarFile = gradleCacheDir.walkTopDown()
        .filter { it.name.startsWith("sqlite-") && it.name.endsWith(".aar") }
        .firstOrNull()

    val sqliteFrameworkAarFile = gradleCacheDir.walkTopDown()
        .filter { it.name.startsWith("sqlite-framework-") && it.name.endsWith(".aar") }
        .firstOrNull()

    // Ensure the Timber .aar file was found
    if (timberAarFile != null && roomAarFile != null && sqliteAarFile != null && sqliteFrameworkAarFile != null) {
        val timberJar = zipTree(timberAarFile).matching {
            include("classes.jar")
        }.singleFile

        // Include classes from the extracted classes.jar of Timber
        from(zipTree(timberJar))

        // Relocate Timber and other dependencies
        relocate("timber.log", "com.opticon.opticonnect.timber")

        val roomRuntimeJar = zipTree(roomAarFile).matching {
            include("classes.jar")
        }.singleFile

        from(zipTree(roomRuntimeJar))

        relocate("androidx.room", "com.opticon.opticonnect.androidx.room")

        val sqliteJar = zipTree(sqliteAarFile).matching {
            include("classes.jar")
        }.singleFile

        from(zipTree(sqliteJar))

        val sqliteFrameworkJar = zipTree(sqliteFrameworkAarFile).matching {
            include("classes.jar")
        }.singleFile

        from(zipTree(sqliteFrameworkJar))

        relocate("androidx.sqlite", "com.opticon.opticonnect.androidx.sqlite")

        relocate("dagger", "com.opticon.opticonnect.dagger")
        relocate("javax.inject", "com.opticon.opticonnect.javax.inject")
        relocate("jakarta", "com.opticon.opticonnect.jakarta")
        relocate("com.jakewharton", "com.opticon.opticonnect.jakewharton")
    } else {
        logger.warn("Timber .aar file not found in the Gradle cache.")
    }

    exclude("_COROUTINE/**")
    exclude("bleshadow/**")
    exclude("win32-x86/**")
    exclude("win32-x86-64/**")
    exclude("androidx/annotation/**")
    exclude("androidx/arch/**")
    exclude("androidx/collection/**")
    exclude("androidx/concurrent/**")
    exclude("androidx/lifecycle/**")
    exclude("kotlin/**")
    exclude("kotlinx/**")
    exclude("com/google/**")
    exclude("io/**")
    exclude("junit/**")
    exclude("net/**")
    exclude("org/jetbrains/**")
    exclude("org/reactivestreams/**")
    exclude("org/hamcrest/**")
    exclude("org/intellij/**")
    exclude("org/junit/**")
    exclude("org/objenesis/**")
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

tasks.register<org.jetbrains.dokka.gradle.DokkaTask>("dokkaHtmlCustom") {
    outputDirectory.set(layout.buildDirectory.dir("docs/html"))

    dokkaSourceSets {
        named("main") {
            includes.from("src/main/kotlin", "src/main/java")
            noAndroidSdkLink.set(false)
            reportUndocumented.set(true)
            skipDeprecated.set(false)
            jdkVersion.set(17)
        }
    }
}