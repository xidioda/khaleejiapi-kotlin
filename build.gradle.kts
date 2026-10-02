plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.serialization") version "2.1.0"
    `maven-publish`
    `java-library`
}

group = "dev.khaleejiapi"
version = "1.1.0"

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-client-core:3.1.1")
    implementation("io.ktor:ktor-client-cio:3.1.1")
    implementation("io.ktor:ktor-client-content-negotiation:3.1.1")
    implementation("io.ktor:ktor-serialization-kotlinx-json:3.1.1")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.8.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.10.1")

    testImplementation(kotlin("test"))
    testImplementation("io.ktor:ktor-client-mock:3.1.1")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
}

kotlin {
    jvmToolchain(17)
}

java {
    withSourcesJar()
    withJavadocJar()
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])

            pom {
                name.set("KhaleejiAPI SDK")
                description.set("Official Kotlin SDK for KhaleejiAPI — MENA region developer APIs")
                url.set("https://khaleejiapi.dev")

                licenses {
                    license {
                        name.set("MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }

                developers {
                    developer {
                        id.set("khaleejiapi")
                        name.set("KhaleejiAPI Team")
                        email.set("support@khaleejiapi.dev")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/xidioda/khaleejiapi-kotlin.git")
                    developerConnection.set("scm:git:ssh://github.com/xidioda/khaleejiapi-kotlin.git")
                    url.set("https://github.com/xidioda/khaleejiapi-kotlin")
                }
            }
        }
    }
}

tasks.test {
    useJUnitPlatform()
}
