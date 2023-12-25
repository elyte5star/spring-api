plugins {
	java
	id("org.springframework.boot") version "3.2.1"
	id("io.spring.dependency-management") version "1.1.4"
	id("org.asciidoctor.jvm.convert") version "3.3.2"
}

group = "com.elyte"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

extra["snippetsDir"] = file("build/generated-snippets")


dependencies {
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-amqp")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("jakarta.persistence:jakarta.persistence-api:3.1.0")
	implementation("com.google.code.gson:gson:2.10.1")
	implementation("com.maxmind.geoip2:geoip2:4.2.0")
	implementation("org.wiremock:wiremock:3.3.1")
	implementation("com.maxmind.db:maxmind-db:3.1.0")
	implementation("com.jcabi:jcabi-matchers:1.7.0")
	implementation("org.junit.jupiter:junit-jupiter:5.10.1")
    implementation("jakarta.validation:jakarta.validation-api:3.0.2")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.2.0")
	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")
	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
	implementation("org.springframework.boot:spring-boot-starter-freemarker:3.2.0")
	implementation("nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect:3.3.0")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-mail")
	implementation("org.jasypt:jasypt-springsecurity3:1.9.3")
	implementation("org.thymeleaf.extras:thymeleaf-extras-springsecurity6")
	implementation("com.stripe:stripe-java:24.9.0-beta.1")
	implementation("io.jsonwebtoken:jjwt:0.9.0")
	implementation("jakarta.xml.bind:jakarta.xml.bind-api:4.0.1")
	implementation("org.glassfish.external:glassfish-jaxb:10.0-b28")
	compileOnly("org.projectlombok:lombok")
	implementation("com.fasterxml.jackson.core:jackson-databind:2.16.0-rc1")
    implementation("com.jwebmp.jackson.datatype:jackson-datatype-jsr310:0.63.0.19")
	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
	runtimeOnly("com.mysql:mysql-connector-j")
	runtimeOnly("org.postgresql:postgresql")
	annotationProcessor("org.projectlombok:lombok")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.springframework.amqp:spring-rabbit-test")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")
	testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.test {
    project.property("snippetsDir")?.let { outputs.dir(it) }
}



tasks.asciidoctor {
    project.property("snippetsDir")?.let { inputs.dir(it) }
    dependsOn(tasks.test)
}
