plugins {
    id("com.android.application")
    kotlin("android")
}

android {
    namespace = "com.trabalho.listasdetarefas"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.trabalho.listasdetarefas"
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
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
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.24")

    // Core + KTX (traz NotificationCompat também via androidx.core)
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.core:core:1.13.1")

    // Activity (mesmo usando Activity base, esta lib é útil)
    implementation("androidx.activity:activity-ktx:1.9.2")

    // Wear (views)
    implementation("androidx.wear:wear:1.3.0")
}