//val kodeinVersion = "6.4.0-dev+"
val kodeinVersion = "6.5.1"

plugins {
    java
    kotlin("jvm") version "1.3.61"
    `maven-publish`
}

group = "yokiano.codecontroller"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven(url = "https://dl.bintray.com/kodein-framework/Kodein-DI")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Kotlin Related
    api("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.0")
    api("io.github.microutils", "kotlin-logging", "1.7.6")
    // Kodein
    api("org.kodein.di", "kodein-di-generic-jvm", kodeinVersion)
    api("org.kodein.di", "kodein-di-framework-tornadofx-jvm", kodeinVersion)
    // TornadoFX
    api("no.tornado", "tornadofx", "1.7.19")
    api("no.tornado:tornadofx-controlsfx:0.1")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}


publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "yokiano"
            artifactId = "codecontroller"
            version = "1.0-SNAPSHOT"

            from(components["java"])
        }
    }
}