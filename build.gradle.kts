plugins {
    java
    id("org.springframework.boot") version "3.1.5"
    id("io.spring.dependency-management") version "1.1.3"
    id("org.springdoc.openapi-gradle-plugin") version "1.8.0"
    id("io.freefair.lombok") version "8.4"
    id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.elyte"

version = "0.0.1-SNAPSHOT"

java { sourceCompatibility = JavaVersion.VERSION_21 }

repositories { mavenCentral() }

extra["snippetsDir"] = file("build/generated-snippets")
extra["springCloudAzureVersion"] = "5.6.0"

dependencies {
    //implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
    implementation("org.springframework.data:spring-data-rest-repository:1.0.0.RELEASE")
    implementation("org.hibernate:hibernate-core:6.4.0.CR1")
    //implementation("org.springframework.data:spring-data-jdbc:3.1.5")

    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-api:2.2.0")
    implementation("org.springdoc:springdoc-openapi-starter-common:2.2.0")

    // //implementation("javax.inject:javax.inject:1")
    //implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.azure.spring:spring-cloud-azure-starter")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    testImplementation("org.springframework.security:spring-security-test")
    implementation("org.springframework.boot:spring-boot-starter-web")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
}


dependencyManagement {
    imports {
        mavenBom(
                "com.azure.spring:spring-cloud-azure-dependencies:${property("springCloudAzureVersion")}"
        )
    }
}

tasks.withType<Test> { useJUnitPlatform() }

tasks.test {
    project.property("snippetsDir")?.let { outputs.dir(it) }
}

tasks.asciidoctor {
    project.property("snippetsDir")?.let { inputs.dir(it) }
    dependsOn(tasks.test)
}
