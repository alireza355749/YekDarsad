import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ksp)
}

// =========================================================
// Local properties
// =========================================================

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val supabaseUrl =
    localProperties.getProperty("SUPABASE_URL", "")

val supabasePublishableKey =
    localProperties.getProperty("SUPABASE_PUBLISHABLE_KEY", "")

// =========================================================
// Android
// =========================================================

android {
    namespace = "com.example.yekdarsad"

    compileSdk {
        version = release(36) {
            minorApiLevel = 1
        }
    }

    defaultConfig {
        applicationId = "com.example.yekdarsad"

        minSdk = 29
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"

        buildConfigField(
            "String",
            "SUPABASE_URL",
            "\"$supabaseUrl\""
        )

        buildConfigField(
            "String",
            "SUPABASE_PUBLISHABLE_KEY",
            "\"$supabasePublishableKey\""
        )

        testInstrumentationRunner =
            "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }
}

// =========================================================
// KSP
// =========================================================

ksp {
    arg("room.generateKotlin", "false")
}

// =========================================================
// Dependencies
// =========================================================

dependencies {

    // =====================================================
    // Compose
    // =====================================================

    implementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    implementation(
        libs.androidx.activity.compose
    )

    implementation(
        libs.androidx.compose.material3
    )

    implementation(
        libs.androidx.navigation.compose
    )

    implementation(
        "androidx.compose.material:material-icons-extended"
    )

    implementation(
        libs.androidx.compose.ui
    )

    implementation(
        libs.androidx.compose.ui.graphics
    )

    implementation(
        libs.androidx.compose.ui.tooling.preview
    )

    implementation(
        libs.androidx.core.ktx
    )

    implementation(
        libs.androidx.lifecycle.runtime.ktx
    )

    // =====================================================
    // Room
    // =====================================================

    implementation(
        libs.androidx.room.runtime
    )

    implementation(
        libs.androidx.room.ktx
    )

    ksp(
        "androidx.room:room-compiler:2.8.0"
    )

    // =====================================================
    // Reorderable
    // =====================================================

    implementation(
        libs.reorderable
    )

    // =====================================================
    // DataStore
    // =====================================================

    implementation(
        libs.androidx.datastore.preferences
    )

    // =====================================================
    // Supabase
    // =====================================================

    implementation(
        platform(
            libs.supabase.bom
        )
    )

    implementation(
        libs.supabase.postgrest
    )

    implementation(
        libs.supabase.auth
    )

    implementation(
        libs.ktor.client.android
    )

    // =====================================================
    // Tests
    // =====================================================

    testImplementation(
        libs.junit
    )

    androidTestImplementation(
        platform(
            libs.androidx.compose.bom
        )
    )

    androidTestImplementation(
        libs.androidx.compose.ui.test.junit4
    )

    androidTestImplementation(
        libs.androidx.espresso.core
    )

    androidTestImplementation(
        libs.androidx.junit
    )

    // =====================================================
    // Debug
    // =====================================================

    debugImplementation(
        libs.androidx.compose.ui.test.manifest
    )

    debugImplementation(
        libs.androidx.compose.ui.tooling
    )
}