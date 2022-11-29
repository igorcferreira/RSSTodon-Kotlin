plugins {
    kotlin("plugin.serialization") version "1.7.21"
    kotlin("multiplatform")
    id("com.android.library")
    id("org.jetbrains.kotlinx.kover") version "0.6.1"
}

kover {
    verify {
        onCheck.set(true)
    }
}

kotlin {
    val ktorVersion = "2.1.3"

    jvm()
    android()
    sourceSets {
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmTest by getting {
            dependsOn(commonTest)
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("io.mockk:mockk:1.13.2")
                implementation("io.ktor:ktor-client-mock:$ktorVersion")
            }
        }
        val androidTest by getting {
            dependsOn(commonTest)
        }
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
            }
        }
        val jvmMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-cio:$ktorVersion")
            }
        }
        val androidMain by getting {
            dependsOn(commonMain)
            dependencies {
                implementation("io.ktor:ktor-client-okhttp:$ktorVersion")
            }
        }
    }
}

android {
    namespace = "dev.igorcferreira.rsstodon.api"
    compileSdk = 33
    defaultConfig {
        minSdk = 24
        targetSdk = 33
    }
}
