plugins {
    // Apply the Kotlin JVM plugin to add support for Kotlin on the JVM.
    kotlin("jvm").version("1.3.31")

    // Apply the application plugin to add support for building a CLI application.
    application
}

repositories {
    jcenter()
}

dependencies {
    // Use the Kotlin JDK 8 standard library.
    implementation(kotlin("stdlib-jdk8"))

    implementation("com.google.code.gson:gson:2.8.5")

    testImplementation("junit:junit:4.12")
    testImplementation(kotlin("test"))
}

application {
    // Define the main class for the application.
    mainClassName = "OpeningHours.AppKt"
}