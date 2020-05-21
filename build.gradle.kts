import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import com.jfrog.bintray.gradle.BintrayExtension
import org.gradle.api.publish.maven.MavenPom

val kodeinVersion = "6.5.5"

plugins {
    java
    kotlin("jvm") version "1.3.72"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    `maven-publish`
    id("com.jfrog.bintray") version "1.8.4"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "yokiano"
version = "0.0.5"
val artifactID = "code-controller"

val shadowJar: ShadowJar by tasks
shadowJar.apply {
    archiveBaseName.set(artifactID)
}

repositories {
    mavenCentral()
    jcenter()
}

javafx {
    version = "11.0.2"
    modules = listOf("javafx.controls")
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))

    // Kotlin Related
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.0")
    implementation("io.github.microutils", "kotlin-logging", "1.7.6")
    // Kodein
    implementation("org.kodein.di", "kodein-di-generic-jvm", kodeinVersion)
    implementation("org.kodein.di", "kodein-di-framework-tornadofx-jvm", kodeinVersion)
    // TornadoFX
    implementation("no.tornado", "tornadofx", "1.7.19")
    implementation("no.tornado:tornadofx-controlsfx:0.1")

    // java-diff-utils
    implementation("io.github.java-diff-utils", "java-diff-utils", "4.7")

}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }

}
java {
    withSourcesJar()
    withJavadocJar()
}

fun MavenPom.addDependencies() = withXml {
    asNode().appendNode("dependencies").let { depNode ->
        configurations.compile.get().allDependencies.forEach {
            depNode.appendNode("dependency").apply {
                appendNode("groupId", it.group)
                appendNode("artifactId", it.name)
                appendNode("version", it.version)
            }
        }
    }
}

val publicationName = "mavenPub"
publishing {
    publications {
        register(publicationName, MavenPublication::class) {
            from(components["java"])
            groupId = project.group.toString()
            artifactId = artifactID
            version = project.version.toString()
//            pom.addDependencies()
        }
    }
}

bintray {
    user = System.getenv("BINTRAY_USER")
    key = System.getenv("BINTRAY_API_KEY")
    setPublications(publicationName)
    pkg(delegateClosureOf<BintrayExtension.PackageConfig> {
        repo = "my-tools"
        name = "code-controller"
        userOrg = "yokiano"
        vcsUrl = "https://github.com/yokiano/code_controller"
        version(delegateClosureOf<BintrayExtension.VersionConfig> {
            name = project.version.toString()
        })
    })
}
