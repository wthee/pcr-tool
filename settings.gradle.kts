dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        jcenter() // Warning: this repository is going to shut down soon
        maven("https://dl.bintray.com/umsdk/release")
        maven("https://www.jitpack.io")
    }
}
rootProject.name = "pcr-tool-compose"
include("Material-Calendar-View", ":app")
 