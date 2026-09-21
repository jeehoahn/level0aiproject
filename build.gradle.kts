plugins {
    id("java")
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    // Spring Boot 웹
    implementation("org.springframework.boot:spring-boot-starter-web")

    // WebSocket
    implementation("org.springframework.boot:spring-boot-starter-websocket")

    // Jackson JSON
    implementation("com.fasterxml.jackson.core:jackson-databind")

    // 테스트
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}