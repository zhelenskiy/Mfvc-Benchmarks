import me.champeau.jmh.JmhBytecodeGeneratorTask
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    id("me.champeau.jmh") version "0.7.2"
    id("kotlin")
}

group = "org.example"
version = "1.0-SNAPSHOT"

dependencies {
    implementation(project(":"))
    testImplementation(platform("org.junit:junit-bom:5.11.3"))
    testImplementation("org.junit.jupiter:junit-jupiter")
}

tasks.test {
    useJUnitPlatform()
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(23))
        vendor.set(JvmVendorSpec.ORACLE) // Valhalla build
    }
}

tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(listOf("--release", "23", "--enable-preview"))
}

tasks.withType<JmhBytecodeGeneratorTask>().configureEach {
    jvmArgs = listOf("--enable-preview")
}

jmh {
    resultsFile.set(project.file("${project.rootDir}/results/jmh/Hotspot-Valhalla.txt"))
    jvmArgsPrepend.addAll("--enable-preview")
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        languageVersion = "2.0"
        kotlinOptions.freeCompilerArgs = listOf("-Xvalue-classes", "-Xvalhalla-value-classes", "-Xjvm-enable-preview")
    }
}
