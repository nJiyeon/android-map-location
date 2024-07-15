pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven { url = uri("https://devrepo.kakao.com/nexus/content/groups/public/") }
        maven { url = uri("https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")}
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven (url = "https://devrepo.kakao.com/nexus/content/groups/public/" )
        maven (url = "https://devrepo.kakao.com/nexus/repository/kakaomap-releases/")
    }
}


rootProject.name = "android-map-location"
include(":app")
