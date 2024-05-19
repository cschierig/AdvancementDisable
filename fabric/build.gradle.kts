import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.fabricmc.loom.task.RemapJarTask
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    idea
    java
    `maven-publish`
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.shadow)
}

val modId: String by project
val recipeViewer: String by project
val common = project(":common")


dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    modImplementation(libs.fabric.loader)

    shadow(libs.night.config)
    implementation(libs.night.config)

    implementation(project(":common"))
}

loom {
    val awPath = common.file("src/main/resources/${modId}.accesswidener")
    if (awPath.exists()) {
        accessWidenerPath.set(awPath)
    }
    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }
    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run")
        }
        getByName("server") {
            client()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run")
        }
    }
}

tasks.withType<JavaCompile> {
    source(project(":common").sourceSets.main.get().allSource)
}

tasks.withType<ProcessResources> {
    from(project(":common").sourceSets.main.get().resources)
}

tasks.named<ShadowJar>("shadowJar") {
    configurations = listOf(project.configurations.shadow.get())
    exclude("META-INF")
}

tasks.withType<RemapJarTask>() {
    dependsOn(tasks.getByName("shadowJar"))
    mustRunAfter(tasks.getByName("shadowJar"))
    inputFile = tasks.getByName<ShadowJar>("shadowJar").archiveFile
}

if (System.getenv("MODRINTH_TOKEN") != null) {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("advancementdisable")
        versionNumber.set(project.version.toString())
        versionName.set(project.version.toString() + " - " + project.name.uppercaseFirstChar())
        uploadFile.set(tasks.named("remapJar"))
        syncBodyFrom.set(rootProject.file("README.md").readText())
        gameVersions.set(listOf(libs.versions.minecraft.get()))
        loaders.set(listOf("fabric", "quilt"))
        detectLoaders.set(false)
        changelog.set(file("../CHANGELOG.md").readText())
    }
}
