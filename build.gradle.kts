plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.nexus.publish)
}

repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.jsonic)
    implementation(libs.jetbrains.annotations)
    implementation(libs.guava)
    implementation(libs.slf4j.api)
    testImplementation(libs.testng)
    testImplementation(libs.junit4)
    testImplementation(libs.hamcrest.core)
    testImplementation(libs.hamcrest.library)
    testImplementation(libs.mockito)
    testImplementation(libs.logback)
}

group = "org.omegat"
version = "0.6-2"
description = "language-detector"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.jar {
    manifest {
        attributes("Automatic-Module-Name" to "org.omegat.languagedetector")
    }
}

publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])
            pom {
                name.set("language-detector")
                description.set("language-detector")
                url.set("https://github.com/omegat-org/language-detector")
                licenses {
                    license {
                        name.set("Apache License, Version 2.0")
                        url.set("https://www.apache.org/licenses/")
                        distribution.set("repo")
                    }
                }
                developers {
                    developer {
                        id.set("miurahr")
                        name.set("Hiroshi Miura")
                        email.set("miurahr@linux.com")
                    }
                    developer {
                        name.set("Nakatani Shuyo")
                    }
                    developer {
                        name.set("Fabian Kessler")
                    }
                    developer {
                        name.set("François ROLAND")
                    }
                    developer {
                        name.set("Robert Theis")
                    }
                }
                scm {
                    connection.set("scm:git:git://github.com/omegat-org/language-detector.git")
                    developerConnection.set("scm:git:git://github.com/omegat-org/language-detector.git")
                    url.set("https://github.com/omegat-org/language-detector")
                }
            }
        }
    }
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.add("-Xlint:deprecation")
    options.compilerArgs.add("-Xlint:unchecked")
}

tasks.withType<Javadoc>() {
    options.encoding = "UTF-8"
    setFailOnError(false)
}

tasks.named<Test>("test") {
    useTestNG()
}

val signKey = listOf("signingKey", "signing.keyId", "signing.gnupg.keyName").find {project.hasProperty(it)}
tasks.withType<Sign> {
    onlyIf { signKey != null }
}

signing {
    when (signKey) {
        "signingKey" -> {
            val signingKey: String? by project
            val signingPassword: String? by project
            useInMemoryPgpKeys(signingKey, signingPassword)
        }
        "signing.keyId" -> {
        }
        "signing.gnupg.keyName" -> {
            useGpgCmd()
        }
    }
    sign(publishing.publications["mavenJava"])
}

val ossrhUsername: String? by project
val ossrhPassword: String? by project
nexusPublishing.repositories {
    sonatype {
        nexusUrl.set(uri("https://ossrh-staging-api.central.sonatype.com/service/local/"))
        snapshotRepositoryUrl.set(uri("https://central.sonatype.com/repository/maven-snapshots/"))
        if (ossrhUsername != null && ossrhPassword != null) {
            username.set(ossrhUsername)
            password.set(ossrhPassword)
        } else {
            username.set(System.getenv("SONATYPE_USER"))
            password.set(System.getenv("SONATYPE_PASS"))
        }
    }
}
