pluginManagement {

    repositories {
        gradlePluginPortal()
        mavenCentral()

        // Modstitch
        maven("https://maven.isxander.dev/releases/")

        // Loom platform
        maven("https://maven.fabricmc.net/")

        // MDG platform
        maven("https://maven.neoforged.net/releases/")

        // Stonecutter
        maven("https://maven.kikugie.dev/releases")
        maven("https://maven.kikugie.dev/snapshots")

        maven ("https://maven.parchmentmc.org")
    }
}

plugins {
    id("dev.kikugie.stonecutter") version "0.9.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version("1.0.0")
}

stonecutter {
    kotlinController = true
    centralScript = "build.gradle.kts"

    create(rootProject) {
        /**
         * @param mcVersion The base minecraft version.
         * @param loaders A list of loaders to target, supports "fabric" (1.14+), "neoforge"(1.20.6+), "vanilla"(any) or "forge"(<=1.20.1)
         */
        fun mc(mcVersion: String, name: String = mcVersion, loaders: Iterable<String>) =
            loaders.forEach { version("$name-$it", mcVersion) }

        // Configure your targets here!
        mc("26.2", loaders = listOf("fabric", "neoforge"))
        mc("26.1.2", loaders = listOf("neoforge"))
        mc("1.21.11", loaders = listOf("neoforge", "fabric"))
        mc("1.21.10", loaders = listOf("fabric", "neoforge"))
        mc("1.21.8", loaders = listOf("fabric", "neoforge"))
        mc("1.21.4", loaders = listOf("fabric", "neoforge"))
        mc("1.21.1", loaders = listOf("fabric", "neoforge"))
        mc("1.20.1", loaders = listOf("forge", "fabric"))


        // This is the default target.
        // https://stonecutter.kikugie.dev/stonecutter/guide/setup#settings-settings-gradle-kts
        vcsVersion = "1.21.1-neoforge"
    }
}

rootProject.name = "NirvanaLib"
includeBuild("modstitch")