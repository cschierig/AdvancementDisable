import java.text.SimpleDateFormat
import java.util.*

plugins {
    alias(libs.plugins.neoforge.moddev) apply false
    alias(libs.plugins.fabric.loom) apply false
}

val modArchiveName: String by project
val modName: String by project
val modId: String by project
val modGroup: String by project
val author: String by project
val modVersion: String by project

allprojects {
    val libs = rootProject.libs
    version = "${modVersion}+${libs.versions.minecraft.get()}"
    group = modGroup
}

subprojects {
    val libs = rootProject.libs
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    configure<BasePluginExtension> {
        archivesName.set("${modArchiveName}-${project.name}")
    }

    repositories {
        mavenCentral()
        exclusiveContent {
            forRepository {
                maven("https://api.modrinth.com/maven") {
                    name = "Modrinth"
                }
            }
            filter {
                includeGroup("maven.modrinth")
            }
        }
        exclusiveContent {
            forRepository {
                maven("https://maven.terraformersmc.com/") {
                    name = "TerraformersMC"
                }
            }
            filter {
                includeGroup("com.terraformersmc")
            }
        }
        exclusiveContent {
            forRepository {
                maven("https://repo.spongepowered.org/repository/maven-public") {
                    name = "Sponge"
                }
            }
            filter { includeGroupAndSubgroups("org.spongepowered") }
        }
        maven("https://maven.blamejared.com") {
            name = "BlameJared"
        }
    }

    configure<JavaPluginExtension> {
        toolchain.languageVersion.set(JavaLanguageVersion.of(libs.versions.java.get()))
        sourceCompatibility = JavaVersion.VERSION_25
        targetCompatibility = JavaVersion.VERSION_25

        withSourcesJar()
        withJavadocJar()
    }

    tasks.withType<JavaCompile>().configureEach {
        options.encoding = "UTF-8"
        options.release.set(25)
    }

    tasks.withType<Jar>().configureEach {
        from("LICENSE") {
            rename { "${it}_${modName}" }
        }
    }

    tasks.named<Jar>("jar") {
        manifest {
            attributes(
                mapOf(
                    "Specification-Title" to modName,
                    "Specification-Vendor" to author,
                    "Specification-Version" to archiveVersion,
                    "Implementation-Title" to modName,
                    "Implementation-Vendor" to author,
                    "Implementation-Version" to archiveVersion,
                    "Implementation-Timestamp" to SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ").format(Date()),
                    "Timestamp" to System.currentTimeMillis(),
                    "Built-On-Java" to "${System.getProperty("java.vm.version")} (${System.getProperty("java.vm.vendor")})",
                    "Built-On-Minecraft" to libs.versions.minecraft.get()
                )
            )
        }
    }

    tasks.withType<ProcessResources> {
        val versions = mapOf(
            "version_fabricloader" to rootProject.libs.versions.fabric.loader.get(),
            "version_minecraft" to rootProject.libs.versions.minecraft.get(),
            "version_java" to rootProject.libs.versions.java.get()
        )

        filesMatching(listOf("pack.mcmeta", "fabric.mod.json", "META-INF/neoforge.mods.toml", "*.mixins.json")) {
            expand(project.properties)
            expand(versions)
        }
    }
}

tasks.register("release") {
    dependsOn(project("fabric").tasks.named("modrinth").get())
    dependsOn(project("neoforge").tasks.named("modrinth").get())
    dependsOn(project("fabric").tasks.named("modrinthSyncBody").get())
}

tasks.register("releaseCurseforge") {
    dependsOn(project("fabric").tasks.named("curseforge").get())
    dependsOn(project("neoforge").tasks.named("curseforge").get())
    dependsOn(project("fabric").tasks.named("curseforgeSyncBody").get())
}
