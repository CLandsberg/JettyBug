plugins {
    id("java")
}

group = "de.scoop"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.eclipse.jetty.ee10:jetty-ee10-servlet:12.0.16")
    implementation("ch.qos.logback:logback-classic:1.5.12")
}