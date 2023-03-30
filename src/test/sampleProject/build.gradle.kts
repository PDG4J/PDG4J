plugins {
    id("java")
}

group = "ru.hse"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("com.google.guava:guava:24.0-jre")
    testImplementation(platform("org.junit:junit-bom:5.9.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

java {
    sourceCompatibility = JavaVersion.VERSION_16
    targetCompatibility = JavaVersion.VERSION_16
}

tasks.register("printClasspath") {
    println(configurations.asMap.keys)
    println(configurations["compileClasspath"].asPath)
}

tasks.register<Copy>("copyClasspath") {
    from(configurations["runtimeClasspath"])
    into(File(buildDir, "dependencies"))
}

tasks.test {
    useJUnitPlatform()
}