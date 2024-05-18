plugins {
    idea
    java
    alias(libs.plugins.fabric.loom)
}

val modName: String by project
val modId: String by project
val author: String by project
val version: String by project

tasks.forEach {
    it.group = null
}

dependencies {
    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    compileOnly(libs.mixin)

    compileOnly(libs.night.config)
}

loom {
    val awPath = file("src/main/resources/${modId}.accesswidener")
    if (awPath.exists()) {
        accessWidenerPath.set(awPath)
    }

    mixin {
        defaultRefmapName.set("${modId}.refmap.json")
    }
}
