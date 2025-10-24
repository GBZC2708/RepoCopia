plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.gms.google-services")

    // Crashlytics
    id("com.google.firebase.crashlytics")
    id("com.google.dagger.hilt.android")
    id("kotlin-kapt")
}

android {
    namespace = "com.example.alphakids"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.alphakids"
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
}

dependencies {

    // --- DEPENDENCIAS BÁSICAS (Usando libs.versions.toml) ---
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-compiler:2.51.1")
    // Compose integration for HiltViewModel()
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Iconos
    implementation("androidx.compose.material:material-icons-extended")

    // --- FIREBASE (Usando BoM para manejar versiones) ---
    // Importa el BoM MÁS RECIENTE UNA SOLA VEZ
    implementation(platform("com.google.firebase:firebase-bom:34.4.0"))

    // Añade las dependencias de Firebase SIN especificar versión
    implementation("com.google.firebase:firebase-analytics") // Analytics (opcional pero común)
    implementation("com.google.firebase:firebase-ai") // Firebase AI (Gemini)
    implementation("com.google.firebase:firebase-firestore") // Firestore

    // 1. Crashlytics
    implementation("com.google.firebase:firebase-crashlytics-ndk")

    // 2. Auth
    implementation("com.google.firebase:firebase-auth")

    // --- COROUTINES & VIEWMODEL ---
    // Para la extensión .await() con Tasks de Play Services
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.8.1")
    // ViewModels (versiones explícitas o puedes ponerlas en libs.versions.toml)
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.3")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.3") // Para viewModel() en Compose

    // --- NAVEGACIÓN ---
    implementation("androidx.navigation:navigation-compose:2.7.7")

    // Camera X
    val cameraXVersion = "1.3.4"
    implementation("androidx.camera:camera-camera2:$cameraXVersion")
    implementation("androidx.camera:camera-lifecycle:$cameraXVersion")
    implementation("androidx.camera:camera-view:$cameraXVersion")

    // 1. Accompanist Permissions
    implementation("com.google.accompanist:accompanist-permissions:0.36.0")

}
