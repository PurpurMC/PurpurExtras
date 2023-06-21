import java.net.URL
import java.nio.file.Files

val serverDir: File = projectDir.resolve("run")
val pluginDir: File = serverDir.resolve("plugins")

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven {
        name = "Sonatype OSS"
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        name = "PurpurMC"
        url = uri("https://repo.purpurmc.org/snapshots")
    }

    maven {
        name = "PaperMC"
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://libraries.minecraft.net")
    }
    exclusiveContent {
        forRepository {
            maven {
                name = "Jitpack"
                url = uri("https://jitpack.io")
            }
        }
        filter {
            includeGroup("com.github.YouHaveTrouble")
        }
    }
    maven {
        url = uri("https://jitpack.io")
    }
    exclusiveContent {
        forRepository {
            mavenCentral()
        }
        filter {
            includeGroup("org.reflections")
            includeGroup("org.junit.jupiter")
            includeGroup("dev.jorel")
        }
    }
    mavenCentral()
}

dependencies {
    api("com.github.YouHaveTrouble:Entiddy:v2.0.1")
    api("org.reflections:reflections:0.10.2")
    compileOnly("org.purpurmc.purpur:purpur-api:1.20-R0.1-SNAPSHOT")
    implementation("dev.jorel:commandapi-bukkit-shade:9.0.3")

    testCompileOnly("org.purpurmc.purpur:purpur-api:1.20-R0.1-SNAPSHOT")
    testImplementation("org.junit.jupiter:junit-jupiter:5.9.3")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.9.3")
    testCompileOnly("org.junit.jupiter:junit-jupiter-params:5.9.2")
}

group = "org.purpurmc.purpurextras"
version = "1.27.0"
description = "\"This should be a plugin\" features from Purpur"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

tasks {
    test {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    clean {
        doLast {
            serverDir.deleteRecursively()
        }
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "name" to project.name,
                    "version" to project.version,
                    "description" to project.description!!.replace('"'.toString(), "\\\"")
                )
            )
        }
    }

    shadowJar {
        archiveFileName.set("PurpurExtras-${version}.jar")
        relocate("org.reflections", "org.purpurmc.purpurextras.reflections")
        relocate("me.youhavetrouble.entiddy", "org.purpurmc.purpurextras.entiddy")
        relocate("dev.jorel.commandapi", "org.purpurmc.purpurextras.commandapi")
    }

    register("downloadServer") {//TODO: Automatically check server build / version to check for download
        group = "purpur"
        doFirst {
            serverDir.mkdirs()
            pluginDir.mkdirs()
            if(serverDir.resolve("server.jar").exists())
                serverDir.resolve("server.jar").delete()
            URL("https://api.purpurmc.org/v2/purpur/1.20.1/latest/download").openStream().use {
                Files.copy(it, serverDir.resolve("server.jar").toPath())
            }
        }
    }

    register("runServer", JavaExec::class) {
        group = "purpur"
        dependsOn("shadowJar")
        if (!serverDir.resolve("server.jar").exists()) {
            dependsOn("downloadServer")
        }
        doFirst {
            pluginDir.resolve("PurpurExtras.jar").delete()
            Files.copy(
                buildDir.resolve("libs").resolve("PurpurExtras-${version}.jar").toPath(),
                pluginDir.resolve("PurpurExtras.jar").toPath()
            )
        }
        classpath = files(serverDir.resolve("server.jar"))
        workingDir = serverDir
        jvmArgs = listOf("-Dcom.mojang.eula.agree=true")
        args = listOf("--nogui")
        standardInput = System.`in`
    }

}

