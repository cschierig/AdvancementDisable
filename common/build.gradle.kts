plugins {
    alias(libs.plugins.neoforge.moddev)
}

val modId: String by project
val enabledPlatforms: String by project

neoForge {
    neoFormVersion = libs.versions.neoforge.neoform.get()
    // Automatically enable AccessTransformers if the file exists
    val atFile = file("src/main/resources/META-INF/accesstransformer.cfg")
    if (atFile.exists()) {
        accessTransformers.from(atFile)
    }
}

dependencies {
    compileOnly(libs.mixin)
    compileOnly(libs.mixinextras.common)
    annotationProcessor(libs.mixinextras.common)

    compileOnly(libs.night.config)
}

configurations {
    create("commonJava") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
    create("commonResources") {
        isCanBeResolved = false
        isCanBeConsumed = true
    }
}

artifacts {
    add("commonJava", sourceSets.main.get().java.sourceDirectories.singleFile)
    add("commonResources", sourceSets.main.get().resources.sourceDirectories.singleFile)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Implement mcgradleconventions loader attribute
val loaderAttribute = Attribute.of("io.github.mcgradleconventions.loader", String::class.java)
for (variant in arrayOf("apiElements", "runtimeElements", "sourcesElements", "javadocElements")) {
    configurations.named(variant) {
        attributes {
            attribute(loaderAttribute, "common")
        }
    }
}

sourceSets.configureEach {
    for (variant in arrayOf(compileClasspathConfigurationName, runtimeClasspathConfigurationName)) {
        configurations.named(variant) {
            attributes {
                attribute(loaderAttribute, "common")
            }
        }
    }
}
