import io.spring.gradle.dependencymanagement.dsl.DependencyManagementExtension
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import org.springframework.boot.gradle.dsl.SpringBootExtension
import org.springframework.boot.gradle.tasks.run.BootRun

plugins {
    val kotlinVersion = "1.2.40"
    val springBootVersion = "2.0.1.RELEASE"
    val springDependencyManagement = "1.0.5.RELEASE"
    val dockerPluginVersion = "0.19.2"

    base
    id("org.jetbrains.kotlin.jvm") version kotlinVersion
    id("org.springframework.boot") version springBootVersion apply false
    id("org.jetbrains.kotlin.plugin.spring") version kotlinVersion apply false
    id("org.jetbrains.kotlin.plugin.noarg") version kotlinVersion apply false
    id("io.spring.dependency-management") version springDependencyManagement apply false
    id("com.palantir.docker") version dockerPluginVersion apply false
}

subprojects {

//    if (name.startsWith("shared")) {
//        apply { plugin("org.gradle.sample.hello") }
//    } else  {
//        apply { plugin("org.gradle.sample.goodbye") }
//    }

    apply {
        plugin("org.jetbrains.kotlin.jvm")
        plugin("org.springframework.boot")
        plugin("org.jetbrains.kotlin.plugin.spring")
        plugin("org.jetbrains.kotlin.plugin.noarg")
        plugin("io.spring.dependency-management")
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://repo.spring.io/libs-milestone") }
    }

    configure<DependencyManagementExtension> {
        val springCloudVersion: String = "Finchley.M9"
        val springCloudStreamVersion = "Elmhurst.RELEASE"

        imports {
            mavenBom("org.springframework.cloud:spring-cloud-dependencies:$springCloudVersion")
            mavenBom("org.springframework.cloud:spring-cloud-stream-dependencies:$springCloudStreamVersion")
        }
    }

    dependencies {
        // kotlin
        compile(kotlin("stdlib-jdk8"))
        compile(kotlin("reflect"))

        // Web
        compile("org.springframework.boot:spring-boot-starter-webflux")
        compile("com.fasterxml.jackson.module:jackson-module-kotlin")
        compile("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")

        // Testing
        testCompile("io.projectreactor:reactor-test")
        testCompile("org.springframework.boot:spring-boot-starter-test") {
            exclude(module = "junit")
        }
        testImplementation("org.junit.jupiter:junit-jupiter-api")
        testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine")

        // Tooling
        compileOnly("org.springframework:spring-context-indexer")
        compile("org.springframework.boot:spring-boot-devtools")
        compile("org.springframework.boot:spring-boot-starter-actuator")
        compile("io.micrometer:micrometer-registry-prometheus")

        compileOnly("org.springframework.boot:spring-boot-configuration-processor")
    }

    tasks.withType<KotlinCompile> {
        kotlinOptions {
            jvmTarget = org.gradle.api.JavaVersion.VERSION_1_8.toString()
            freeCompilerArgs = listOf("-Xjsr305=strict")
        }
    }

    val test by tasks.getting(Test::class) {
        useJUnitPlatform {
            includeTags ("fast", "smoke")
            excludeTags ("slow", "ci")
            includeEngines ("junit-jupiter")
            excludeEngines ("junit-vintage")
        }
        failFast = true
        testLogging {
            events ("passed", "skipped", "failed")
        }
    }

    configure<SpringBootExtension> {
        buildInfo()
    }
}
