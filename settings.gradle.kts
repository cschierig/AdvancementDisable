pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://maven.fabricmc.net/") {
                    name = "Fabric"
                }
            }
            filter {
                includeGroupAndSubgroups("net.fabricmc")
            }
        }
    }
}

val modName: String by extra
rootProject.name = modName
include("common", "fabric", "neoforge")
