plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services") // Or the latest version // ✅ Add this line
    id("kotlin-kapt") // <-- Add this line

}

android {


    namespace = "com.helloworld.universalschoolteacher"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.helloworld.universalschoolteacher"
        minSdk = 24
        targetSdk = 36
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

    packaging {
        resources {
            excludes += "META-INF/DEPENDENCIES"
        }
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(platform("com.google.firebase:firebase-bom:34.1.0"))
    implementation (libs.firebase.auth)
    implementation (libs.firebase.database)
    implementation("androidx.navigation:navigation-compose:2.9.3")
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.recyclerview)
    implementation("com.github.bumptech.glide:glide:4.16.0")
    kapt(libs.compiler)
    implementation("com.github.dhaval2404:imagepicker:2.1")
    implementation("com.google.firebase:firebase-storage-ktx:21.0.1")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.google.firebase:firebase-messaging:23.2.0")
    implementation("com.google.auth:google-auth-library-oauth2-http:1.16.0")
    implementation("com.google.code.gson:gson:2.10.1")

    // Use this for Kotlin
    implementation(libs.material)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

}