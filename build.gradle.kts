import org.json.JSONObject
import java.net.URL
import java.nio.file.Files

val serverDir: File = projectDir.resolve("run")
val pluginDir: File = serverDir.resolve("plugins")

buildscript {
    dependencies {
        classpath("org.json:json:20090211")
    }
}

plugins {
    `java-library`
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

repositories {
    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.purpurmc.org/snapshots")
    }

    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://libraries.minecraft.net")
    }

    maven {
        url = uri("https://jitpack.io")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    api("com.github.YouHaveTrouble:Entiddy:v2.0.1")
    api("org.reflections:reflections:0.10.2")
    compileOnly("org.purpurmc.purpur:purpur-api:1.20-R0.1-SNAPSHOT")
}

group = "org.purpurmc.purpurextras"
version = "1.27.0"
description = "\"This should be a plugin\" features from Purpur"
java.sourceCompatibility = JavaVersion.VERSION_17
java.targetCompatibility = JavaVersion.VERSION_17

tasks {

    compileJava {
        options.encoding = "UTF-8"
    }

    javadoc {
        options.encoding = "UTF-8"
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(
                mapOf(
                    "name" to project.name,
                    "version" to project.version,
                    "description" to (project.description!!.replace('"'.toString(), "\\\""))
                )
            )
        }
    }

    shadowJar {
        relocate("org.reflections", "org.purpurextras.reflections")
        relocate("me.youhavetrouble.entiddy", "org.purpurextras.entiddy")
    }

    register("downloadServer") {
        group = "purpur"
        doFirst {
            serverDir.mkdirs()
            pluginDir.mkdirs()
            val obj = JSONObject(URL("https://api.purpurmc.org/v2/purpur/1.20.1").readText(Charsets.UTF_8))
            val latest = obj.getJSONObject("builds").getInt("latest")
            URL("https://api.purpurmc.org/v2/purpur/1.20.1/${latest}/download").openStream().use {
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
                buildDir.resolve("libs").resolve("PurpurExtras-${version}-all.jar").toPath(),
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

