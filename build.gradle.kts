plugins {
    `java-library`
    `maven-publish`
    signing
    id("io.github.gradle-nexus.publish-plugin") version  "2.0.0-rc-1"
}

repositories {
    mavenCentral()
}

dependencies {
    api("net.arnx:jsonic:1.2.11")
    api("org.jetbrains:annotations:23.0.0")
    api("com.google.guava:guava:32.0.0-jre")
    api("org.slf4j:slf4j-api:2.0.7")
    testImplementation("org.testng:testng:7.7.0")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.hamcrest:hamcrest-core:1.3")
    testImplementation("org.hamcrest:hamcrest-library:1.3")
    testImplementation("org.mockito:mockito-all:1.9.5")
    testImplementation("ch.qos.logback:logback-classic:1.4.12")
}

group = "org.omegat"
version = "0.6-2-SNAPSHOT"
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
                        url.set("https://http://www.apache.org/licenses/")
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
            // default signatory - do nothing()
            // please set
            // signing.keyId = 0xAAAAAA
            // signing.password = "signature passphrase"
            // secretKeyRingFile = "secring.gpg"
            // e.g. gpg --export-secret-keys > secring.gpg
        }
        "signing.gnupg.keyName" -> {
            useGpgCmd()
        }
    }
    sign(publishing.publications["mavenJava"])
}

nexusPublishing.repositories.sonatype {
    val sonatypeUsername: String? by project
    val sonatypePassword: String? by project
    nexusUrl.set(uri("https://s01.oss.sonatype.org/service/local/"))
    snapshotRepositoryUrl.set(uri("https://s01.oss.sonatype.org/content/repositories/snapshots/"))
    username.set(sonatypeUsername)
    password.set(sonatypePassword)
}
