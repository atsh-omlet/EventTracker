plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.cs360.eventtrackeratsushi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.cs360.eventtrackingappatsushi"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation (libs.recyclerview)
    implementation(libs.preference)
    implementation(libs.work.runtime)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation (libs.material)
    implementation (libs.lifecycle.viewmodel.ktx)
    implementation (libs.lifecycle.livedata.ktx)
}