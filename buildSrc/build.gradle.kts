plugins {
    `kotlin-dsl`
    id("com.diffplug.spotless") version "6.11.0"
}

repositories {
    gradlePluginPortal()
}

spotless {
    kotlinGradle {
        target("*.gradle.kts", "src/main/kotlin/*.gradle.kts")
        ktlint()
    }
}

dependencies {
    implementation("org.jetbrains.kotlin.jvm:org.jetbrains.kotlin.jvm.gradle.plugin:1.6.21")
    implementation("com.gradle.plugin-publish:com.gradle.plugin-publish.gradle.plugin:1.0.0")
    implementation("com.diffplug.spotless:com.diffplug.spotless.gradle.plugin:6.11.0")
}
