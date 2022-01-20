import org.gradle.api.tasks.testing.logging.TestLogEvent.FAILED
import org.gradle.api.tasks.testing.logging.TestLogEvent.SKIPPED
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.jetbrains.kotlin.jvm") version ("1.6.10")
    id("org.springframework.boot") version ("2.6.2")
    id("io.spring.dependency-management") version ("1.0.11.RELEASE")
    id("com.google.cloud.tools.jib") version ("3.0.0")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://repo.spring.io/milestone")
    maven("https://repo.spring.io/snapshot")
    maven("https://jitpack.io")
}

dependencyManagement {
    imports {
        mavenBom("io.r2dbc:r2dbc-bom:${properties["r2dbc_bom_version"]}")
    }
}

dependencies {
//    developmentOnly("org.springframework.boot:spring-boot-devtools")
//    implementation("org.jetbrains.kotlin:kotlin-stdlib")
    implementation("org.springframework.fu:spring-fu-kofu:${properties["spring_kofu_version"]}")
//    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
//    implementation("org.springframework.boot:spring-boot-starter-mail")
//    implementation("org.springframework.boot:spring-boot-starter-thymeleaf")
    implementation("org.springframework.boot:spring-boot-starter-security")
//    implementation("org.springframework.security:spring-security-data")
    implementation("org.springframework.data:spring-data-r2dbc")
    implementation("io.r2dbc:r2dbc-h2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("io.jsonwebtoken:jjwt-impl:${properties["jsonwebtoken_version"]}")
    implementation("io.jsonwebtoken:jjwt-jackson:${properties["jsonwebtoken_version"]}")
    implementation("am.ik.yavi:yavi:${properties["yavi_version"]}")
//    implementation("org.zalando:problem-spring-webflux:${properties["zalando_problem_version"]}")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("io.projectreactor:reactor-test")
    implementation("org.apache.commons:commons-lang3:${properties["commons_lang3_version"]}")
//    testImplementation("com.github.hitanshu-dhawan:JsonDSL:1.0.3")
//    testImplementation("com.nhaarman.mockitokotlin2:mockito-kotlin:2.2.0")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit") {
        exclude("junit", "junit")
    }
    implementation(platform("org.jetbrains.kotlin:kotlin-bom"))
}

configurations {
    implementation.configure {
        listOf(
            listOf(
                "org.junit.vintage",
                "junit-vintage-engine"
            ),
            listOf(
                "org.springframework.boot",
                "spring-boot-starter-tomcat"
            ),
            listOf("org.apache.tomcat")
        ).forEach {
            if (it.size == 2)
                exclude(
                    group = it.first(),
                    module = it.last()
                )
            else exclude(group = it.first())
        }
    }
}


tasks.withType<KotlinCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
        freeCompilerArgs = listOf("-Xjsr305=strict", "-Xjvm-default=enable")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    testLogging { events(FAILED, SKIPPED) }
    reports {
        html.isEnabled = true
        ignoreFailures = true
    }
}

//tasks.named("check") {
//    dependsOn("BddTests")
//}

//tasks.register<Test>("BddTests") {
//    description = "Execute cucumber BDD tests."
//    group = "verification"
//    include("**/*Bdd*")
//    reports {
//        html.isEnabled = true
//        ignoreFailures = true
//    }
//}

