plugins {
    id("com.android.application")
}

val releaseStorePath = System.getenv("ANDROID_KEYSTORE_FILE")

android {
    namespace = "com.junhong.onething"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.junhong.onething"
        minSdk = 26
        targetSdk = 35
        versionCode = 3
        versionName = "1.0.2"
    }

    signingConfigs {
        create("release") {
            if (releaseStorePath != null) {
                storeFile = file(releaseStorePath)
                storePassword = System.getenv("ANDROID_KEYSTORE_PASSWORD")
                keyAlias = System.getenv("ANDROID_KEY_ALIAS")
                keyPassword = System.getenv("ANDROID_KEY_PASSWORD")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets.getByName("main").assets.srcDir(layout.buildDirectory.dir("generated/webAssets"))
}

val syncWebAssets by tasks.registering(Sync::class) {
    from(file("../..")) {
        include("index.html", "styles.css", "app.js", "model.js", "icon.svg", "manifest.webmanifest", "sw.js")
    }
    into(layout.buildDirectory.dir("generated/webAssets"))
}

tasks.named("preBuild").configure { dependsOn(syncWebAssets) }

dependencies {
    implementation("androidx.webkit:webkit:1.12.1")
}
