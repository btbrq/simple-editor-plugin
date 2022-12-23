import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

fun properties(key: String) = project.findProperty(key).toString()

plugins {
    id("java")
    id("groovy")
    id("org.jetbrains.kotlin.jvm") version "1.7.21"
    id("org.jetbrains.intellij") version "1.10.1"
}

group = "com.github.btbrq.simpleeditorplugin"
version = "1.0.0"

repositories {
    mavenCentral()
}

kotlin {
    jvmToolchain(11)
}

intellij {
    pluginName.set(properties("pluginName"))
    version.set(properties("platformVersion"))
    type.set(properties("platformType"))
    updateSinceUntilBuild.set(false)

    plugins.set(properties("platformPlugins").split(',').map(String::trim).filter(String::isNotEmpty))
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.codehaus.groovy:groovy-all:2.5.14")
    testImplementation("org.spockframework:spock-core:1.2-groovy-2.5")
}

val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    jvmTarget = "1.8"
}
val compileTestKotlin: KotlinCompile by tasks
compileTestKotlin.kotlinOptions {
    jvmTarget = "1.8"
}

tasks {
    patchPluginXml {
        if (System.getenv("CHANGE_NOTES_PATH") != null) {
            changeNotes.set(file(System.getenv("CHANGE_NOTES_PATH")).readText())
        }
    }

    signPlugin {
        if (System.getenv("CERTIFICATE_CHAIN_PATH") != null && System.getenv("PRIVATE_KEY_PATH") != null) {
            certificateChain.set(file(System.getenv("CERTIFICATE_CHAIN_PATH")).readText())
            privateKey.set(file(System.getenv("PRIVATE_KEY_PATH")).readText())
            password.set(System.getenv("PRIVATE_KEY_PASSWORD"))
        }
    }

    publishPlugin {
        token.set(System.getenv("ORG_GRADLE_PROJECT_intellijPublishToken"))
    }
}
