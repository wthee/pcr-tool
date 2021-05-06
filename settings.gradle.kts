dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://dl.bintray.com/umsdk/release")
        maven("https://www.jitpack.io")
    }
}
rootProject.name = "pcr-tool-compose"
include(":app")
 