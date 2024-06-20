val modId: String by project
val enabledPlatforms: String by project

architectury {
    common(enabledPlatforms.split(','))
}

loom {
    val awFile = file("src/commonAssets/resources/${modId}.accesswidener")
    if (awFile.exists()) {
        accessWidenerPath.set(awFile)
    }

    addRemapConfiguration("testModImplementation") {
        targetConfigurationName.set("test")
        onCompileClasspath = true
        onRuntimeClasspath = true
    }
}

dependencies {
    modImplementation(libs.fabric.loader)

    "testModImplementation"(libs.fabric.loader)
    testImplementation(libs.fabric.loader.junit)

    compileOnly(libs.night.config)
}

sourceSets {
    create("commonAssets") {
        resources {
            srcDir(file("src/commonAssets/generated"))
            exclude("src/commonAssets/generated/.cache")
        }
    }
}


tasks.withType<Test> {
    useJUnitPlatform()
}
