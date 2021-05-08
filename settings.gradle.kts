dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://repo1.maven.org/maven2/")
        maven("https://www.jitpack.io")
    }
}
rootProject.name = "pcr-tool-compose"
include(":app")
 