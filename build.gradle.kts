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
}

kotlin {
    jvmToolchain(21)
}