import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	val kotlinVersion = "1.6.21"
	id("org.springframework.boot") version "2.7.0"
	id("io.spring.dependency-management") version "1.0.11.RELEASE"
	id("org.asciidoctor.convert") version "1.5.12"
	kotlin("jvm") version kotlinVersion
	kotlin("plugin.spring") version kotlinVersion
	kotlin("plugin.jpa") version kotlinVersion
	kotlin("kapt") version kotlinVersion
}

group = "mash-up.ggiriggiri"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_13

repositories {
	mavenCentral()
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {

	implementation("org.springframework.boot:spring-boot-starter-web")
	testImplementation("org.springframework.boot:spring-boot-starter-test")

	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("org.springframework.boot:spring-boot-starter-data-redis")
//	implementation("org.springframework.boot:spring-boot-starter-validation")
//	runtimeOnly("org.mariadb.jdbc:mariadb-java-client")

//	implementation("org.springframework.boot:spring-boot-starter-security")
//	testImplementation("org.springframework.security:spring-security-test")
//	implementation("org.springframework.boot:spring-boot-starter-oauth2-client")


//	implementation("org.springframework.boot:spring-boot-starter-actuator")
//	runtimeOnly("io.micrometer:micrometer-registry-prometheus")
	implementation("org.redisson:redisson-spring-boot-starter:3.17.3")

	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

	runtimeOnly("mysql:mysql-connector-java")

	// h2
	runtimeOnly("com.h2database:h2:2.1.212")

	// queryDsl
	implementation("com.querydsl:querydsl-jpa:5.0.0")
	kapt("com.querydsl:querydsl-apt:5.0.0:jpa")

	// test
	testImplementation("com.h2database:h2")

	// documentation
	asciidoctor("org.springframework.restdocs:spring-restdocs-asciidoctor")
	testImplementation("org.springframework.restdocs:spring-restdocs-mockmvc")

}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs = listOf("-Xjsr305=strict")
		jvmTarget = "13"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

val snippetsDir = file("build/generated-snippets")

tasks.asciidoctor {
	inputs.dir(snippetsDir)
	dependsOn("restDocsTest")
}


tasks.register("restDocs") {
	dependsOn("asciidoctor")
	doLast {
		copy {
			from(file("$buildDir/asciidoc/html5"))
			into(file("docs"))
		}
	}
}

tasks.register<Test>("restDocsTest") {
	useJUnitPlatform()
	filter {
		includeTestsMatching("*ControllerTest")
	}
}