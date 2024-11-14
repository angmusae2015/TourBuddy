plugins {
    alias(libs.plugins.android.application)
    // Google services gradle plugin 추가
    id("com.google.gms.google-services")
}

android {
    namespace = "com.tourbuddy.app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.tourbuddy.app"
        minSdk = 34
        targetSdk = 34
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
    viewBinding {
        enable = true;
    }
}

dependencies {
    // Firebase BoM 불러오기
    implementation(platform(libs.firebase.bom))
    implementation("com.google.firebase:firebase-auth")

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}