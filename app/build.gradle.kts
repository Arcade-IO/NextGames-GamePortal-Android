plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")
}

android {
    namespace = "dk.nextgames.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "dk.nextgames.app"
        minSdk = 24
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
}

dependencies {
    implementation(platform(libs.firebase.bom))
    implementation(libs.google.firebase.database)
    implementation(libs.firebase.auth)

    // Compose BOM fra version-catalog-et
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.coil.compose)
    implementation(libs.kotlinx.coroutines.play.services)
    // Compose-basis
    implementation(libs.ui)
    implementation(libs.compose.ui.text)        // ← NY linje
    implementation(libs.androidx.ui.text)
    // Material 3 via version-catalog (den beholdes)
    //implementation(libs.androidx.material3)
    implementation("androidx.compose.material3:material3:1.0.1")
    implementation(libs.androidx.material)
    implementation(libs.androidx.material.icons.extended)

    // Navigation + ViewModel
    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.palette.ktx)

    implementation(libs.androidx.datastore.preferences)
}