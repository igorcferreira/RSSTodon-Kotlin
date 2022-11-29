import org.jetbrains.compose.internal.getLocalProperty

fun Project.loadProperty(name: String, fallbackEnv: String): String = findProperty(name)?.toString()
    ?: getLocalProperty(name)
    ?: System.getenv(fallbackEnv)
    ?: ""

val instance = project.loadProperty("instance", "RSSTODON_INSTANCE")
val clientId = project.loadProperty("clientId", "RSSTODON_CLIENT_ID")
val clientSecret = project.loadProperty("clientSecret","RSSTODON_CLIENT_SECRET")
val scope  = project.loadProperty("scope","RSSTODON_SCOPE")
val redirectScheme =  project.loadProperty("redirectScheme", "RSSTODON_REDIRECT_SCHEME")

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.compose") version "1.2.1"
    id("kotlin-kapt")
    id("kotlin-android")
}

android {
    namespace = "dev.igorcferreira.rsstodon.android"
    compileSdk = 33

    defaultConfig {
        applicationId = "dev.igorcferreira.rsstodon.android"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        buildConfigField("String", "INSTANCE", "\"$instance\"")
        buildConfigField("String", "CLIENT_ID", "\"$clientId\"")
        buildConfigField("String", "CLIENT_SECRET", "\"$clientSecret\"")
        buildConfigField("String", "SCOPE", "\"$scope\"")
        buildConfigField("String", "REDIRECT_SCHEME", "\"$redirectScheme\"")

        manifestPlaceholders["redirectScheme"] = redirectScheme
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.1.1"
    }
    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

@OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
dependencies {
    val roomVersion = "2.4.3"
    implementation(compose.foundation)
    implementation(compose.material)
    implementation(compose.material3)
    implementation(compose.ui)
    implementation(compose.runtime)
    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.5.1")
    implementation("androidx.activity:activity-compose:1.6.1")
    implementation("androidx.security:security-crypto:1.1.0-alpha04")
    implementation("com.google.android.material:material:1.7.0")
    implementation("androidx.compose.ui:ui-tooling:1.4.0-alpha02")
    implementation("androidx.compose.material:material:1.3.1")
    implementation("androidx.activity:activity-ktx:1.7.0-alpha02")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
    implementation(project(":api"))
    implementation(project(":ui"))
    implementation("androidx.browser:browser:1.4.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.4")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.0")
    implementation("androidx.room:room-runtime:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
}