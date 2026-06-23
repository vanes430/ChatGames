val pluginVersion = "1.2.1"

plugins {
    java
}

group = "chatgames"
version = pluginVersion

// ─── Dependencies ───
repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.helpch.at/releases")
}

dependencies {
    compileOnly("io.papermc.paper:paper-api:1.21.11-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.12.2")
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(25))
}

// ─── Deploy ───
val pluginsDir = "/var/lib/pterodactyl/volumes/d64a444d-cbe1-44eb-99f6-1aa116292bef/plugins"
tasks.register<Copy>("deploy") {
    dependsOn(tasks.jar)

    from(layout.buildDirectory.file("libs/ChatGames-${pluginVersion}.jar"))
    into(pluginsDir)

    doLast {
        val deployedFile = file("$pluginsDir/ChatGames-${pluginVersion}.jar")
        if (deployedFile.exists()) {
            println("✅ Deployed to ${deployedFile.absolutePath}")
            println("   Size: ${deployedFile.length()} bytes")
        }
    }
}
