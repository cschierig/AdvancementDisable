import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import net.darkhax.curseforgegradle.TaskPublishCurseForge
import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    id("multiloader-loader")
    alias(libs.plugins.fabric.loom)
    alias(libs.plugins.shadow)
    alias(libs.plugins.minotaur)
    alias(libs.plugins.curseforgegradle)
}

val modId: String by project
val modGroup: String by project
val withApiJar = property("withApiJar").toString().toBoolean()
val withSourcesJar = property("withSourcesJar").toString().toBoolean()
val compatMods = property("compatMods").toString().toBoolean()
val modrinthId: String by project
val modrinthType: String by project
val curseforgeId: String by project
val withExampleMod = property("withExampleMod").toString().toBoolean()

val commonProject = project(":common")

val shadowBundle by configurations.creating
configurations {
    "shadowBundle" {
        isCanBeResolved = true
        isCanBeConsumed = false
    }
}

dependencies {
    minecraft(libs.minecraft)

    implementation(libs.fabric.loader)
    implementation(libs.night.config)

    "shadowBundle"(libs.night.config)

    if (compatMods) {
        implementation(libs.compat.modmenu.fabric)
        implementation(libs.fabric.api)
    }
}


sourceSets {
    if (withExampleMod) {
        create("example") {
            // TODO: only add api to compileClasspath
            compileClasspath += sourceSets.main.get().compileClasspath
            runtimeClasspath += sourceSets.main.get().runtimeClasspath

            resources {
                srcDir(file("src/example/generated"))
                exclude("src/example/generated/resources/.cache")
            }
        }
    }
}

loom {
    val awPath = commonProject.file("src/assets/resources/${modId}.accesswidener")
    if (awPath.exists()) {
        accessWidenerPath.set(awPath)
    }
    runs {
        getByName("client") {
            client()
            configName = "Fabric Client"
            ideConfigGenerated(true)
            runDir("run/client")
        }
        getByName("server") {
            server()
            configName = "Fabric Server"
            ideConfigGenerated(true)
            runDir("run/server")
        }
        create("datagen") {
            inherit(getByName("client"))
            name("Data Generation")
            vmArg("-Dfabric-api.datagen")
            vmArg("-Dfabric-api.datagen.output-dir=${commonProject.file("src/main/assets/generated")}")
            vmArg("-Dfabric-api.datagen.modid=$modId")

            runDir("build/datagen")
        }
        if (withExampleMod) {
            create("example") {
                client()
                configName = "Example Mod"
                ideConfigGenerated(true)
                source(project.sourceSets.getByName("example"))
                runDir("run/exampleClient")
            }
            create("exampleServer") {
                server()
                configName = "Example Mod Server"
                ideConfigGenerated(true)
                source(project.sourceSets.getByName("example"))
                runDir("run/exampleServer")
            }
            create("exampleDatagen") {
                inherit(getByName("example"))
                name("Example Mod Data Generation")
                vmArg("-Dfabric-api.datagen")
                vmArg("-Dfabric-api.datagen.output-dir=${file("src/example/generated")}")
                vmArg("-Dfabric-api.datagen.modid=${modId}_example")

                runDir("build/exampleDatagen")
            }
        }
    }
}

tasks.getByName<ShadowJar>("shadowJar") {
    configurations = listOf(shadowBundle)
    archiveClassifier.set("")
}

if (withApiJar) {
    tasks.register<Jar>("apiJar") {
        archiveClassifier.set("api")
        dependsOn(tasks.named("jar"))
        from(zipTree(tasks.named("jar").get().outputs.files.asPath))
        include("fabric.mod.json")
        include("*.mixins.json")
        include("${modGroup.replace('.', '/')}/api/**")
    }

    tasks.named("build") {
        dependsOn(tasks.named("apiJar"))
    }
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
        uploadFile.set(tasks.named("shadowJar"))
        additionalFiles.set(files.map { tasks.named(it) })
        syncBodyFrom.set(rootProject.file("README.md").readText())
        dependencies {
        }
        gameVersions.set(listOf(libs.versions.minecraft.get()))
        loaders.set(listOf("fabric"))
        detectLoaders.set(false)
        changelog.set(file("../CHANGELOG.md").readText())
    }
    tasks.named("modrinth") { dependsOn("runDatagen") }
}

if (System.getenv("CURSEFORGE_TOKEN") != null) {
    tasks.register<TaskPublishCurseForge>("curseforge") {
        apiToken = System.getenv("CURSEFORGE_TOKEN")

        upload(curseforgeId, tasks.named("shadowJar")) {
            releaseType = modrinthType
            gameVersions.clear()
            addGameVersion(libs.versions.minecraft.get())
            addModLoader("fabric")
            changelog = file("../CHANGELOG.md").readText()
            changelogType = "markdown"
        }

        disableVersionDetection()
    }

    tasks.named("curseforge") { dependsOn("runDatagen") }
}

// Implement mcgradleconventions loader attribute
val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
for (variant in arrayOf(
    "apiElements",
    "runtimeElements",
    "sourcesElements",
    "javadocElements",
    "includeInternal",
    "modCompileClasspath"
)) {
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "fabric")
        }
    }
}

sourceSets.configureEach {
    for (variant in arrayOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName)) {
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "fabric")
            }
        }
    }
}
