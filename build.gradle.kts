plugins {
    id("java")
}

group = "ru.hse"
version = "1.0-SNAPSHOT"

repositories {
    mavenLocal()
    mavenCentral()
}

dependencies {
    implementation("fr.inria.gforge.spoon:spoon-core:10.3.0-beta-18")
    implementation("fr.inria.gforge.spoon:spoon-control-flow:0.0.2-SNAPSHOT")
    implementation("fr.inria.gforge.spoon:spoon-data-flow:1.0")
    implementation("ch.qos.logback:logback-classic:1.3.5")

    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}