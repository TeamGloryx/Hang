import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.7.10"
    `maven-publish`
    `java-library`
}
group = "net.gloryx"
version = "0.1"

repositories {
    mavenCentral()
    maven("https://dev.gloryx.net/main")
}

dependencies {
    api("com.typesafe:config:1.4.2")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}


val javadoc by tasks.getting(Javadoc::class)

val javadocJar by tasks.creating(Jar::class) {
    from(javadoc)
    archiveClassifier.set("javadoc")
}

val sourcesJar by tasks.creating(Jar::class) {
    from(sourceSets["main"].allSource)

    archiveClassifier.set("sources")
}


publishing {
    repositories {
        maven("https://dev.gloryx.net/dev") {
            credentials {
                username = System.getenv("GLORYX_DEV_USER")
                password = System.getenv("GLORYX_DEV_PASSWORD")
            }
        }
    }
    publications {
        create<MavenPublication>("maven") {
            groupId = "net.gloryx"
            artifactId = "hang"

            this@create.version = rootProject.version.toString()

            pom {
                name.set("Hang")
                description.set("Use HOCON for translating Minecraft mods!")
                url.set("https://gloryx.net/hang")
                developers {
                    developer {
                        id.set("nothen")
                        name.set("Ilya Nothen")
                        email.set("ilya@gloryx.net")
                    }
                }
            }

            from(components["java"])

            artifact(javadocJar)
            artifact(sourcesJar)
        }
    }
}
