plugins {
    kotlin("jvm") version "1.5.31"
}

group = "com.github.mcsim415"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://oss.sonatype.org/content/repositories/central")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    // maven("https://ci.frostcast.net/plugin/repository/everything")
    mavenLocal()
}

dependencies {
    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly(files("craftbukkit-1.8.8-R0.1-SNAPSHOT.jar"))
    // compileOnly("me.confuser:BarAPI:3.5")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.5.31")

    testImplementation("org.junit.jupiter:junit-jupiter-api:5.8.2")
    testImplementation("org.junit.jupiter:junit-jupiter-engine:5.8.2")
    testImplementation("org.mockito:mockito-core:4.2.0")
}

tasks {
    processResources {
        filesMatching("**/*.yml") {
            expand(project.properties)
        }
    }

    test {
        useJUnitPlatform()
    }

    create<Jar>("paperJar") {
        from(sourceSets["main"].output)
        archiveBaseName.set(project.properties["pluginName"].toString())
        archiveVersion.set("")

        manifest {
            attributes["Main-Class"] = "com.github.mcsim415.bordermc.BordermcPlugin"
        }
        from(sourceSets.main.get().output)

        dependsOn(configurations.runtimeClasspath)
        from({
            configurations.runtimeClasspath.get().filter { it.name.endsWith("jar") }.map { zipTree(it) }
        })

        doLast {
            var dest = File(rootDir, ".debug/plugins")
            val pluginName = archiveFileName.get()
            val pluginFile = File(dest, pluginName)
            if (pluginFile.exists()) dest = File(dest, "update")

            copy {
                from(archiveFile)
                into(dest)
            }
        }
    }
}