import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    kotlin("jvm") version "2.2.0"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.2"
}

group = "com.neovibrant.kacey"
version = "1.2.0-SNAPSHOT"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.hamcrest:hamcrest:2.2")
    implementation("junit:junit:4.12")
    testImplementation("org.assertj:assertj-core:3.22.0")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
        jvmTarget.set(JvmTarget.JVM_17)
    }
}

// tasks.withType<Test> {
// 	useJUnitPlatform()
// }

ktlint {
    version.set("1.5.0")
    android.set(false)
    ignoreFailures.set(false)
    reporters {
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.PLAIN)
        reporter(org.jlleitschuh.gradle.ktlint.reporter.ReporterType.CHECKSTYLE)
    }
    filter {
        exclude("**/generated/**")
    }
}
