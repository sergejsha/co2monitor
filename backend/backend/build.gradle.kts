import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version Libs.kotlinVersion
    kotlin("kapt") version Libs.kotlinVersion
    id("org.jetbrains.kotlin.plugin.serialization") version Libs.kotlinVersion
}

group = "de.halfbit"
version = "1.3"

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

    implementation(Libs.mysql)
    implementation(Libs.exposed)
    implementation(Libs.kaml)
    implementation(Libs.kotlinxSerialization)
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

tasks {
    register("buildReleaseJar", Jar::class.java) {
        group = "build"
        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        archiveClassifier.set("release")
        manifest {
            attributes("Main-Class" to "de.halfbit.co2monitor.Main")
        }
        from(configurations.runtimeClasspath.get()
            .onEach { println("packaging: ${it.name}") }
            .map { if (it.isDirectory) it else zipTree(it) })
        from(sourceSets.main.get().output)
    }
}