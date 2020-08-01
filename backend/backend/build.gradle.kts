@file:Suppress("UnstableApiUsage")

import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.gradle.application")
    kotlin("jvm") version Libs.kotlinVersion
    kotlin("kapt") version Libs.kotlinVersion
    id(Libs.sqldelightPlugin) version Libs.sqldelightVersion
    id(Libs.shadowJarPlugin) version Libs.shadowJarPluginVersion
}

group = "de.halfbit"
version = "2.0"

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

sqldelight {
    database("Co2Database") {
        packageName = "de.halfbit.co2monitor"
    }
}

repositories {
    mavenCentral()
    jcenter()
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation(Libs.ktorServerCore)
    implementation(Libs.ktorNetty)

    implementation(Libs.moshi)
    kapt(Libs.moshiCodegen)

    implementation(Libs.graphQlJava)
    implementation(Libs.loggerSimple)

    implementation(Libs.sqliteDriver)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks.withType<Jar> {
    manifest {
        attributes(
            mapOf(
                "Main-Class" to application.mainClassName
            )
        )
    }
}
