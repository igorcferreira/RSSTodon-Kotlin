import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.compose.internal.getLocalProperty
import org.jetbrains.kotlin.gradle.internal.ensureParentDirsCreated

fun Project.loadProperty(name: String, fallbackEnv: String): String = getLocalProperty(name)
    ?: findProperty(name)?.toString()
    ?: System.getenv(fallbackEnv)
    ?: ""

val instance = project.loadProperty("instance", "RSSTODON_INSTANCE")
val clientId = project.loadProperty("clientId", "RSSTODON_CLIENT_ID")
val clientSecret = project.loadProperty("clientSecret","RSSTODON_CLIENT_SECRET")
val scope  = project.loadProperty("scope","RSSTODON_SCOPE")
val redirectScheme =  project.loadProperty("redirectScheme", "RSSTODON_REDIRECT_SCHEME")
val debugBuild = project.loadProperty("debugBuild", "RSSTODON_LOG_ENABLED")

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose") version "1.2.1"
    id("org.jetbrains.dokka")
    id("maven-publish")
}

kotlin {
    jvm {
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(project(":api"))
                implementation(project(":ui"))
                implementation("pt.davidafsilva.apple:jkeychain:1.1.0")
                implementation("org.jsoup:jsoup:1.15.3")
                implementation("androidx.compose.ui:ui-tooling:1.4.0-alpha02")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
            }
        }
    }
}

val config = tasks.create("createConfig") {
    val configMap = mapOf(
        "INSTANCE" to instance,
        "CLIENT_ID" to clientId,
        "CLIENT_SECRET" to clientSecret,
        "SCOPE" to scope,
        "REDIRECT_SCHEME" to redirectScheme
    )

    val codePackage = "dev.igorcferreira.rsstodon.app"
    val filePath = File(projectDir, "/src/jvmMain/kotlin/${codePackage.replace(".", "/")}")
    val file = File(filePath, "BuildConfig.kt")

    file.ensureParentDirsCreated()
    if (file.exists()) { file.delete() }
    if (file.createNewFile()) {
        file.writeText("package $codePackage\n\n")
        file.appendText("object BuildConfig { \n")
        configMap.forEach {
            file.appendText("    const val ${it.key}: String = \"${it.value}\"\n")
        }
        if (debugBuild == "true") {
            file.appendText("    const val DEBUG: Boolean = true\n")
        } else {
            file.appendText("    const val DEBUG: Boolean = false\n")
        }
        file.appendText("}\n")
    }
}

afterEvaluate {
    tasks.getByName("transformCommonMainDependenciesMetadata")
        .dependsOn("createConfig")
}

compose.desktop {
    val version: String by project
    val buildVersion: String by project
    application {
        mainClass = "dev.igorcferreira.rsstodon.app.MainKt"

        buildTypes.release.proguard {
            //Off for now, until KotlinX serialization issues fixed
            isEnabled.set(false)
            obfuscate.set(true)
            configurationFiles.from(project.file("proguard-rules.pro"))
        }

        nativeDistributions {
            targetFormats(TargetFormat.Dmg)

            packageName = "RSStodon"
            packageVersion = version

            description = "App to produce a RSS file for a Mastodon account"
            copyright = "© 2022 Igor Ferreira. All rights reserved."
            vendor = "Igor Ferreira"
            licenseFile.set(project.file("../LICENSE"))

            macOS {
                bundleID = project.loadProperty("bundleId", "RSSTODON_BUNDLE_ID")
                packageBuildVersion = buildVersion
                iconFile.set(project.file("icon.icns"))

                infoPlist {
                    extraKeysRawXml = macExtraPlistKeys
                }

                signing {
                    sign.set(true)
                    identity.set(project.loadProperty("signingIdentity", "RSSTODON_IDENTITY"))
                }
                notarization {
                    appleID.set(project.loadProperty("signingAppleId", "RSSTODON_APPLE_ID"))
                    password.set(project.loadProperty("signingApplePassword", "RSSTODON_APPLE_PASSWORD"))
                    ascProvider.set(project.loadProperty("signingAppleTeamId", "RSSTODON_APPLE_TEAM_ID"))
                }
            }
        }
    }
}

val macExtraPlistKeys: String
    get() = """
      <key>CFBundleURLTypes</key>
      <array>
        <dict>
          <key>CFBundleURLName</key>
          <string>Example deep link</string>
          <key>CFBundleURLSchemes</key>
          <array>
            <string>$redirectScheme</string>
          </array>
        </dict>
      </array>
    """