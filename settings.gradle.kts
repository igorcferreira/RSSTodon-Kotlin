pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
        mavenLocal()
        google()
        mavenCentral()
    }
}

rootProject.name = "RSStodon"
include(":desktop")
include(":api")
include(":ui")
include(":android")
