dependencies {
    // Основные зависимости Spring Boot
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // БД для тестов
    runtimeOnly("com.h2database:h2")

    // Тестовые зависимости
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.assertj:assertj-core")
    testImplementation("org.mockito:mockito-junit-jupiter:5.13.0")
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs = listOf("-javaagent:${configurations.testRuntimeClasspath.get().files.find { it.name.contains("mockito-core") }!!.absolutePath}")
}