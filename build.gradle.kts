plugins {
    id("java")
}

group = "ru.hse"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

dependencies {
    implementation("fr.inria.gforge.spoon:spoon-core:10.3.0-beta-18")
    implementation("fr.inria.gforge.spoon:spoon-control-flow:0.0.2-SNAPSHOT")
    implementation("ch.qos.logback:logback-classic:1.3.5")
    implementation("com.google.guava:guava:24.0-jre")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("info.picocli:picocli:4.7.1")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}