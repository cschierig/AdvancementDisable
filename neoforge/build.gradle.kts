import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
}

val modId: String by project
val withSourcesJar = property("withSourcesJar").toString().toBoolean()
val withApiJar = property("withApiJar").toString().toBoolean()
val modrinthId: String by project
val modrinthType: String by project

val commonProject = project(":common")

architectury {
    platformSetupLoomIde()
    neoForge()
}

val common by configurations.creating
val shadowBundle by configurations.creating
configurations {
    "common" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }

    compileClasspath.get().extendsFrom(common)
    runtimeClasspath.get().extendsFrom(common)
    getByName("developmentNeoForge").extendsFrom(common)

    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

repositories {
    maven("https://maven.neoforged.net/releases") {
        name = "NeoForged"
    }
}

dependencies {
    neoForge(libs.neoforge.mdk)

    common(project(":common", "namedElements")) { isTransitive = false }
    shadowBundle(project(":common", "transformProductionNeoForge"))

    implementation(libs.night.config)
}

tasks.getByName<ShadowJar>("shadowJar") {
    configurations = listOf(shadowBundle)
    archiveClassifier.set("dev-shadow")
}

tasks.withType<RemapJarTask>() {
    inputFile.set(tasks.getByName<ShadowJar>("shadowJar").archiveFile)
    dependsOn(tasks.getByName<ShadowJar>("shadowJar"))
}

if (System.getenv("MODRINTH_TOKEN") != null) {
    val files = ArrayList<String>()
    if (withSourcesJar) {
        files.add("sourcesJar")
    }
    if (withApiJar) {
        files.add("apiJar")
    }

    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set(modrinthId)
        versionNumber.set(project.version.toString())
        versionName.set(project.version.toString() + " - " + project.name.uppercaseFirstChar())
        versionType.set(modrinthType)
        uploadFile.set(tasks.named("remapJar"))
        additionalFiles.set(files.map { tasks.named(it) })
        syncBodyFrom.set(rootProject.file("README.md").readText())
        dependencies {
        }
        gameVersions.set(listOf(libs.versions.minecraft.get()))
        loaders.set(listOf("neoforge"))
        detectLoaders.set(false)
        changelog.set(file("../CHANGELOG.md").readText())
    }
}

if (System.getenv("CURSEFORGE_TOKEN") != null) {
    tasks.create<TaskPublishCurseForge>("curseforge") {
        apiToken = System.getenv("CURSEFORGE_TOKEN")

        upload(1055905, tasks.named("remapJar")) {
            releaseType = modrinthType
            gameVersions.clear()
            addGameVersion(libs.versions.minecraft.get())
            addModLoader("neoforge")
            changelog = file("../CHANGELOG.md").readText()
            changelogType = "markdown"
        }

        disableVersionDetection()
    }
}
