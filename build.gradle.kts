plugins {
    `java-library`
    `maven-publish`
}

group = System.getenv("GROUP") ?: "dev.kodex"
version = "0.2.0"

repositories {
    mavenCentral()
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(25)
    }
    withSourcesJar()
    withJavadocJar()
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
}

dependencies {
    api(libs.pf4j)
    api(libs.okhttp)
    api(libs.jackson.databind)
}

publishing {
    publications {
        create<MavenPublication>("maven") {
            groupId = "dev.kodex"
            artifactId = "spi"
            from(components["java"])

            pom {
                name = "Kodex Plugin SPI"
                description = "Stable SPI for Kodex metadata and content-source plugins."
                url = "https://github.com/kodex-app/spi"
                licenses {
                    license {
                        name = "GNU General Public License v3.0"
                        url = "https://www.gnu.org/licenses/gpl-3.0.txt"
                    }
                }
                scm {
                    url = "https://github.com/kodex-app/spi"
                    connection = "scm:git:https://github.com/kodex-app/spi.git"
                    developerConnection = "scm:git:git@github.com:kodex-app/spi.git"
                }
            }
        }
    }
    // Local-repo publish works out of the box: ./gradlew publishToMavenLocal
    // Public publish is via JitPack (see jitpack.yml): push a tag, consumers pull
    // com.github.kodex-app:kodex-spi:<tag> from https://jitpack.io. Add Maven Central at 1.0 if wanted.
}
