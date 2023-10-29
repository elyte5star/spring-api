plugins {
	java
	id("org.springframework.boot") version "3.1.5"
	id("io.spring.dependency-management") version "1.1.3"
}

group = "com.elyte"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
	mavenCentral()
}

extra["springCloudAzureVersion"] = "5.6.0"

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.data:spring-data-rest-repository:1.0.0.RELEASE")
	implementation("jakarta.persistence:jakarta.persistence-api:3.2.0-B01")
	//implementation("org.springframework.boot:spring-boot-starter-data-neo4j")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("io.springfox:springfox-boot-starter:3.0.0")
	implementation("io.springfox:springfox-swagger-ui:3.0.0")
	//implementation("org.springframework.boot:spring-boot-starter-jdbc:3.1.5")
	//implementation("javax.inject:javax.inject:1")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.azure.spring:spring-cloud-azure-starter")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.security:spring-security-test")
}

dependencyManagement {
	imports {
		mavenBom("com.azure.spring:spring-cloud-azure-dependencies:${property("springCloudAzureVersion")}")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
