plugins {
    kotlin("jvm") version "2.0.0"
}

group = "de.blazemcworld"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("net.minestom:minestom-snapshots:1f34e60ea6")
    implementation("io.github.oshai:kotlin-logging-jvm:6.0.9")
    implementation("org.apache.logging.log4j:log4j-slf4j2-impl:2.23.1")
    implementation("org.apache.logging.log4j:log4j-core:2.23.1")
}

kotlin {
    jvmToolchain(21)
}