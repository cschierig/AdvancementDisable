import org.gradle.kotlin.dsl.support.uppercaseFirstChar

plugins {
    `java-library`
    idea
    `maven-publish`
    alias(libs.plugins.neoforge.gradle)
    alias(libs.plugins.minotaur)
}

val modId: String by project
val recipeViewer: String by project

//jarJar.enable()
dependencies {
    implementation("net.neoforged:neoforge:${libs.versions.neoforge.mdk.get()}")

    compileOnly(project(":common"))
    implementation(libs.night.config)
    // unneeded as night-config ships with neoforge
    //jarJar(libs.night.config)
}

// taken from sodium
// NeoGradle compiles the game, but we don't want to add our common code to the game's code
val notNeoTask: (Task) -> Boolean = { it: Task -> !it.name.startsWith("neo") && !it.name.startsWith("compileService") }

tasks.withType<JavaCompile>().matching(notNeoTask).configureEach {
    source(project(":common").sourceSets.main.get().allSource)
}

tasks.withType<ProcessResources>().matching(notNeoTask).configureEach {
    from(project(":common").sourceSets.main.get().resources)
}

minecraft {
    val atFile = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (atFile.exists()) {
        file(atFile)
    }
}

runs {
    configureEach {
        modSource(project.sourceSets.main.get())
    }
}

if (System.getenv("MODRINTH_TOKEN") != null) {
    modrinth {
        token.set(System.getenv("MODRINTH_TOKEN"))
        projectId.set("advancementdisable")
        versionNumber.set(project.version.toString())
        versionName.set(project.version.toString() + " - " + project.name.uppercaseFirstChar())
        uploadFile.set(tasks.named<Jar>("jar"))
        syncBodyFrom.set(rootProject.file("README.md").readText())
        gameVersions.set(listOf(libs.versions.minecraft.get()))
        loaders.set(listOf("neoforge"))
        detectLoaders.set(false)
        changelog.set(file("../CHANGELOG.md").readText())
    }
}
