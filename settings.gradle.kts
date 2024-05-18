pluginManagement {
    repositories {
        gradlePluginPortal()
        maven("https://maven.fabricmc.net/") {
            name = "Fabric"
        }
        maven("https://maven.neoforged.net/releases") {
            name = "Forge"
        }
    }
}

rootProject.name = "AdvancementDisable"
include("common", "fabric", "neoforge")
