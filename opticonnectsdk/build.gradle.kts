plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.ksp)
    alias(libs.plugins.shadow)
    alias(libs.plugins.dokka)
}

val embedded by configurations.creating {
    isCanBeResolved = false
    isCanBeConsumed = false
}

// Only dependencies added to embedded are bundled into the shaded AAR.
configurations.named("implementation") {
    extendsFrom(embedded)
}

val embeddedRuntimeClasspath by configurations.creating {
    isCanBeResolved = true
    isCanBeConsumed = false
    extendsFrom(embedded)
}

android {
    namespace = "com.opticon.opticonnect.sdk"
    compileSdk = 36
    buildToolsVersion = "36.0.0"

    defaultConfig {
        minSdk = 26
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    testOptions {
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    sourceSets["main"].assets.srcDirs("src/main/assets")
    sourceSets["main"].java.srcDirs("build/generated/ksp/main/kotlin")

    sourceSets["androidTest"].java.srcDirs("src/androidTest/java")
}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_11)
    }
}

dependencies {
    add("embedded", libs.room.runtime.android)
    add("embedded", libs.room.ktx)
    add("embedded", libs.sqlite.android)
    add("embedded", libs.sqlite.framework.android)
    add("embedded", libs.dagger)
    add("embedded", libs.timber)

    implementation(libs.androidx.core)
    ksp(libs.room.compiler)
    ksp(libs.dagger.compiler)

    implementation(libs.rxandroidble)
    api(libs.coroutines)
    implementation(libs.coroutines.android)
    implementation(libs.coroutines.rx3)
    implementation(libs.rxkotlin)

    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.core)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.junit)
    androidTestImplementation(libs.coroutines.test)
}

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

    configurations = listOf()
    from(embeddedRuntimeClasspath.incoming.artifacts.resolvedArtifacts.map { artifacts ->
        artifacts.map { artifact ->
            val artifactFile = artifact.file
            if (artifactFile.extension == "aar") {
                val classesJar = zipTree(artifactFile).matching { include("classes.jar") }.singleFile
                zipTree(classesJar)
            } else {
                zipTree(artifactFile)
            }
        }
    })

    relocate("timber.log", "com.opticon.opticonnect.timber")
    relocate("androidx.room", "com.opticon.opticonnect.androidx.room")
    relocate("androidx.sqlite", "com.opticon.opticonnect.androidx.sqlite")
    relocate("dagger", "com.opticon.opticonnect.dagger")
    relocate("javax.inject", "com.opticon.opticonnect.javax.inject")
    relocate("jakarta", "com.opticon.opticonnect.jakarta")
    relocate("com.jakewharton", "com.opticon.opticonnect.jakewharton")

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
    exclude("org/jspecify/**")
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

    // Set the archive name
    archiveFileName.set("${project.name}.aar")
    destinationDirectory.set(layout.buildDirectory.dir("outputs/aar"))
}

tasks.dokkaHtml {
    moduleName.set("opticonnect sdk")
    dokkaSourceSets {
        configureEach {
            documentedVisibilities.set(
                setOf(org.jetbrains.dokka.DokkaConfiguration.Visibility.PUBLIC)
            )
            includes.from("index.md")
            perPackageOption {
                matchingRegex.set("com\\.opticon\\.opticonnect\\.sdk\\.internal.*")
                suppress.set(true)
            }
        }
    }
}

val customizeDokkaLandingPage by tasks.registering {
    dependsOn("dokkaHtml")

    doLast {
        val indexFile = layout.buildDirectory.file("dokka/html/index.html").get().asFile
        val title = """    <h1 class="cover"><span><span>opticonnect</span></span> <span><span>sdk</span></span></h1>"""
        val titleWithLogo = """
    <div style="display: flex; align-items: center; justify-content: space-between; gap: 32px;">
      <h1 class="cover" style="margin: 0;"><span><span>opticonnect</span></span> <span><span>sdk</span></span></h1>
      <img src="images/opticon_logo_dokka.svg" alt="Opticon" style="width: 200px; max-width: 35%; flex-shrink: 0; transform: translateY(-12px);">
    </div>""".trimEnd()

        val html = indexFile.readText()
        if (title in html) {
            indexFile.writeText(html.replace(title, titleWithLogo))
        }
    }
}

tasks.register<Copy>("copyDokkaImages") {
    dependsOn(customizeDokkaLandingPage)
    from("images")  // Source directory containing your images
    into(layout.buildDirectory.dir("dokka/html/images"))  // Destination in Dokka's output
}

tasks.named("dokkaHtml").configure {
    finalizedBy("copyDokkaImages")
}
