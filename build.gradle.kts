plugins {
    id("java")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("maven-publish")
}

group = "ru.hse"
version = "0.0.1"

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

dependencies {
    implementation("fr.inria.gforge.spoon:spoon-core:10.3.0-beta-18")
    implementation("fr.inria.gforge.spoon:spoon-control-flow:0.0.2-SNAPSHOT")
    implementation("ch.qos.logback:logback-core:1.3.5")
    implementation("ch.qos.logback:logback-classic:1.3.5")
    implementation("com.google.guava:guava:24.0-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("info.picocli:picocli:4.7.1")
    implementation("org.pf4j:pf4j:3.9.0")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.slf4j:slf4j-api:2.0.4")
}

publishing {
    repositories {
        mavenLocal()
    }
    publications {
        create<MavenPublication>(project.name) {
            from(components["java"])
            groupId = "ru.hse"
            version = project.version.toString()
        }
    }
}

tasks.build {
    dependsOn.add(tasks.shadowJar)
}

tasks.shadowJar {
    archiveFileName.set("${project.name}-${project.version}.jar")
    manifest {
        attributes["Main-Class"] = "ru.hse.pdg4j.Main"
    }
}

tasks.jar {
    archiveFileName.set("${project.name}-${project.version}-slim.jar")
}

tasks.test {
    useJUnitPlatform()
}