import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.google.services)
}

val localProperties = Properties()
val localPropertiesFile = rootProject.file("local.properties")
if (localPropertiesFile.exists()) {
    localProperties.load(FileInputStream(localPropertiesFile))
}

// Helper function to safely get property
fun getLocalProperty(key: String) = localProperties.getProperty(key) ?: ""

android {
    namespace = "uk.ac.wlv.petmate"
    compileSdk = 36
    ndkVersion = "28.2.13676358"

    buildFeatures {
        compose = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "uk.ac.wlv.petmate"
        minSdk = 27
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "CLOUDINARY_CLOUD_NAME",
            "\"${getLocalProperty("cloudinary.cloud.name")}\""
        )
        buildConfigField(
            "String",
            "CLOUDINARY_API_KEY",
            "\"${getLocalProperty("cloudinary.api.key")}\""
        )
        buildConfigField(
            "String",
            "CLOUDINARY_API_SECRET",
            "\"${getLocalProperty("cloudinary.api.secret")}\""
        )
        buildConfigField(
            "String",
            "WEB_CLIENT_ID",
            "\"${getLocalProperty("web.client.id")}\""
        )
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


}

kotlin {
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17)
    }
}

dependencies {

    // ================= CORE =================
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    // ================= COMPOSE BOM =================
    implementation(platform(libs.androidx.compose.bom))

    // ================= COMPOSE UI =================
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.runtime)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.compose.foundation.layout)
    implementation(libs.androidx.compose.animation)

    // ================= MATERIAL 3 (IMPORTANT) =================
    implementation("androidx.compose.material3:material3:1.4.0")

    // Icons (Material 2 icons still commonly used in Compose)
    implementation("androidx.compose.material:material-icons-extended")

    // ================= NAVIGATION =================
    implementation(libs.androidx.navigation.compose.v280)

    // ================= LIFECYCLE =================
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.savedstate)
    implementation(libs.androidx.lifecycle.runtime.compose)

    // ================= WORK =================
    implementation(libs.androidx.work.runtime.ktx)

    // ================= FIREBASE =================
    implementation(platform("com.google.firebase:firebase-bom:34.7.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")

    // ================= GOOGLE SERVICES =================
    implementation("com.google.android.gms:play-services-auth:21.5.1")
    implementation(libs.play.services.location)
    implementation(libs.play.services.maps)
    implementation(libs.play.services.basement)
    implementation(libs.play.services.gcm)

    // ================= CREDENTIAL MANAGER =================
    implementation("androidx.credentials:credentials:1.6.0")
    implementation("androidx.credentials:credentials-play-services-auth:1.6.0")
    implementation("com.google.android.libraries.identity.googleid:googleid:1.1.1")

    // ================= DATASTORE =================
    implementation("androidx.datastore:datastore-preferences:1.2.0")

    // ================= IMAGES / UI HELPERS =================
    implementation("com.airbnb.android:lottie-compose:6.3.0")
    implementation("io.coil-kt:coil-compose:2.5.0")

    // ================= NETWORK =================
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // ================= CLOUD =================
    implementation("com.cloudinary:cloudinary-android:3.1.2")

    // ================= MAPS =================
    implementation("org.osmdroid:osmdroid-android:6.1.20")

    // ================= PERMISSIONS =================
    implementation("com.google.accompanist:accompanist-permissions:0.37.3")

    // ================= DEPENDENCY INJECTION (KOIN) =================
    implementation(libs.koin.android)
    implementation(libs.koin.compose)
    implementation(libs.koin.androidx.compose.v411)
    implementation(libs.googleid)
    implementation(libs.androidx.compose.foundation.foundation.layout2)
    implementation(libs.androidx.compose.foundation.foundation3)

    // ================= UI TOOLING =================
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)

    // ================= TESTING =================
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // ================= OTHER =================
    implementation("androidx.core:core-splashscreen:1.2.0")
}