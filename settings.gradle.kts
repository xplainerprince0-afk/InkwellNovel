pluginManagement {
    repositories {
        gradlePluginPortal()
        mavenCentral()
        google()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Inkwell"

include(":app")
include(":core:data")
include(":core:network")
include(":core:common")
include(":feature:editor")
include(":feature:characters")
include(":feature:world")
include(":feature:settings")
include(":feature:camera")
include(":feature:maps")
