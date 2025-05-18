import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("org.springframework.boot") version "3.4.3"
    id("org.jlleitschuh.gradle.ktlint") version "12.1.1"
    id("io.spring.dependency-management") version "1.1.7"

    kotlin("jvm") version "2.0.0"
    kotlin("plugin.spring") version "2.0.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.5")

    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core:9.20.0")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:2.18.3")
    implementation("org.apache.commons:commons-csv:1.12.0")

    testImplementation("org.junit.jupiter:junit-jupiter:5.12.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.mockk:mockk:1.13.16")
    testImplementation("com.ninja-squad:springmockk:4.0.2")
    testImplementation("org.wiremock:wiremock-standalone:3.5.2")
}

tasks.named("compileKotlin", org.jetbrains.kotlin.gradle.tasks.KotlinCompilationTask::class.java) {
    compilerOptions {
        freeCompilerArgs.add("-Xjsr305=strict")
    }
}
tasks
    .withType<org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile>()
    .configureEach {

        compilerOptions.jvmTarget.set(JvmTarget.JVM_21)
    }
kotlin {
    compilerOptions {
        apiVersion.set(org.jetbrains.kotlin.gradle.dsl.KotlinVersion.KOTLIN_2_0)
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

ktlint {
    version.set("1.2.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
