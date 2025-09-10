pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        google()   // MUST include

        mavenCentral()
        maven("https://jitpack.io")

        gradlePluginPortal()
    }

    // ✅ Add this block below repositories
    plugins {
        id("com.google.gms.google-services") version "4.4.1"
        kotlin("jvm") version "2.2.0"
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

dependencyResolutionManagement {

    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io") // Add this

    }


}

rootProject.name = "UniversalSchoolTeacher"
include(":app")
