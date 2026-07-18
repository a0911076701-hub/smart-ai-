import java.util.Properties
import java.io.FileInputStream

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    id("com.google.gms.google-services")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("kotlin-kapt")
}

// قراءة المفاتيح السرية من local.properties (لا تضع مفاتيح حقيقية في هذا الملف مباشرة)
val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}
fun secret(key: String): String = localProperties.getProperty(key) ?: ""

android {
    namespace = "com.yourcompany.appname"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.yourcompany.appname"
        minSdk = 26
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // حقن المفاتيح كـ BuildConfig fields بدل كتابتها في الكود مباشرة
        buildConfigField("String", "GROQ_API_KEY", "\"${secret("GROQ_API_KEY")}\"")
        buildConfigField("String", "GROQ_BASE_URL", "\"${secret("GROQ_BASE_URL")}\"")
        buildConfigField("String", "HUGGINGFACE_API_KEY", "\"${secret("HUGGINGFACE_API_KEY")}\"")
        buildConfigField("String", "HUGGINGFACE_BASE_URL", "\"${secret("HUGGINGFACE_BASE_URL")}\"")
        buildConfigField("String", "CEREBRAS_API_KEY", "\"${secret("CEREBRAS_API_KEY")}\"")
        buildConfigField("String", "CEREBRAS_BASE_URL", "\"${secret("CEREBRAS_BASE_URL")}\"")
        buildConfigField("String", "OPENROUTER_API_KEY", "\"${secret("OPENROUTER_API_KEY")}\"")
        buildConfigField("String", "OPENROUTER_BASE_URL", "\"${secret("OPENROUTER_BASE_URL")}\"")
        buildConfigField("String", "POLLINATIONS_BASE_URL", "\"https://image.pollinations.ai/prompt/\"")
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
            isMinifyEnabled = false
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
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
    // Core / Compose
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.8.4")

    // Hilt
    implementation("com.google.dagger:hilt-android:2.51.1")
    kapt("com.google.dagger:hilt-android-compiler:2.51.1")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")

    // Firebase
    implementation(platform("com.google.firebase:firebase-bom:33.1.2"))
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.android.gms:play-services-auth:21.2.0")

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Coroutines
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Room (نسخ احتياطي محلي)
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-ktx:2.6.1")
    kapt("androidx.room:room-compiler:2.6.1")

    // DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // Coil (تحميل الصور)
    implementation("io.coil-kt:coil-compose:2.6.0")

    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
}
