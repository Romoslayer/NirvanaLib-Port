import deps.DependencyConfig


plugins {
    id("dev.isxander.modstitch.base") version "0.8.4"
    id("dev.isxander.modstitch.shadow") version "0.8.4"
    id("dev.isxander.modstitch.publishing") version "0.8.4"
}

fun prop(name: String, consumer: (prop: String) -> Unit) {
    (findProperty(name) as? String?)?.let(consumer)
}

val modv = "2.2.0"
val mid = "nirvana_lib"

val loader = when {
    modstitch.isLoom -> "fabric"
    modstitch.isModDevGradleRegular -> "neoforge"
    modstitch.isModDevGradleLegacy -> "forge"
    else -> throw IllegalStateException("Unsupported loader")
}

val minecraft = property("deps.minecraft") as String

modstitch {
    minecraftVersion = minecraft

    // Alternatively use stonecutter.eval if you have a lot of versions to target.
    // https://stonecutter.kikugie.dev/stonecutter/guide/setup#checking-versions
    javaVersion = when (minecraft) {
        "1.20.1" -> 17
        "1.21.1" -> 21
        "1.21.4" -> 21
        "1.21.8", "1.21.10", "1.21.11" -> 21
        "26.1.2", "26.2" -> 25
        else -> throw IllegalArgumentException("Please store the java version for ${property("deps.minecraft")} in build.gradle.kts!")
    }

    // If parchment doesnt exist for a version yet you can safely
    // omit the "deps.parchment" property from your versioned gradle.properties
    parchment {
        prop("deps.parchment") {
            if (minecraft == "1.21.1") minecraftVersion.set("1.21")
            mappingsVersion = it
        }
    }

    // This metadata is used to fill out the information inside
    // the metadata files found in the templates folder.

    metadata {
        modId = mid
        modName = "Nirvana Lib"
        modVersion = modv
        modGroup = "com.clefal"
        modAuthor = "Clefal"
        modDescription =
            "Library mod for Clefal"
        modLicense = "MIT"

        fun <K : Any, V : Any> MapProperty<K, V>.populate(
            block: MapProperty<K, V>.() -> Unit
        ) {
            block()
        }

        replacementProperties.populate<String, String> {
            // You can put any other replacement properties/metadata here that
            // modstitch doesn't initially support. Some examples below.
            put("mod_issue_tracker", "https://github.com/TUsama/Loot-Beams-Refork/issues")
            put(
                "pformat", when (property("deps.minecraft")) {
                    "1.20.1" -> 15
                    "1.21.1" -> 34
                    "1.21.4" -> 46
                    "1.21.8" -> 64
                    "1.21.10" -> 69
                    "1.21.11" -> 70.0
                    "26.1.2" -> 84.0
                    "26.2" -> 84.0
                    else -> throw IllegalArgumentException("Please store the resource pack version for ${property("deps.minecraft")} in build.gradle.kts! https://minecraft.wiki/w/Pack_format")
                }.toString()
            )

            put("target_minecraft", minecraft)
            //put("target_lib", property("deps.lib") as String)
            put(
                "target_loader", when (loader) {
                    "neoforge" -> property("deps.neoforge") as String
                    else -> ""
                }
            )
            put("loader", loader)
            put(
                "target_fabricloader", when (loader) {
                    "fabric" -> property("deps.fabric_loader") as String
                    else -> ""
                }
            )

            put("target_forge", findProperty("deps.forge") as? String ?: "")


            put("fzzy_config_version", property("deps.fzzy_config_version") as String)
            put("common_networking", property("deps.common_networking") as String)
        }
    }

    // Fabric Loom (Fabric)
    loom {
        // It's not recommended to store the Fabric Loader version in properties.
        // Make sure its up to date.
        fabricLoaderVersion = if (minecraft == "26.2") "0.19.3" else "0.16.10"
        configureLoom {
            runs {
                all {
                    runDir = "../../run"
                    ideConfigGenerated(true)
                }
                if (minecraft != "26.2") {
                    accessWidenerPath.set(file("../../src/main/resources/${mid}.accesswidener"))
                }
            }
            mixin.useLegacyMixinAp = false

        }
    }

    // ModDevGradle (NeoForge, Forge, Forgelike)
    moddevgradle {
        prop("deps.forge") { forgeVersion = it }
        prop("deps.neoforge") { neoForgeVersion = it }
        prop("deps.mcp") { mcpVersion = it }

        // Configures client and server runs for MDG, it is not done by default
        defaultRuns()

        // This block configures the `neoforge` extension that MDG exposes by default,
        // you can configure MDG like normal from here
        configureNeoForge {
            setAccessTransformers("../../src/main/resources/META-INF/accesstransformer.cfg")
            validateAccessTransformers = false
            runs.all {
                disableIdeRun()
                //if (modstitch.isModDevGradleRegular) gameDirectory = file("../../run")
                //println("gameD is " + gameDirectory.asFileTree.asPath)
            }

        }


    }



    mixin {
        // You do not need to specify mixins in any mods.json/toml file if this is set to
        // true, it will automatically be generated.
        addMixinsToModManifest = true
        when (minecraft) {
            "1.21.10", "1.21.8", "1.21.11", "26.1.2", "26.2" -> configs.register("$mid.new_render")
            else -> configs.register(mid)
        }


        // Most of the time you wont ever need loader specific mixins.
        // If you do, simply make the mixin file and add it like so for the respective loader:
        // if (isLoom) configs.register("examplemod-fabric")
        // if (isModDevGradleRegular) configs.register("examplemod-neoforge")
        // if (isModDevGradleLegacy) configs.register("examplemod-forge")
    }
}
base {
    val meta = modstitch.metadata
    archivesName = "${meta.modName.get()}-${loader}-${minecraft}"
}

// Stonecutter constants for mod loaders.
// See https://stonecutter.kikugie.dev/stonecutter/guide/comments#condition-constants
stonecutter {
    constants.putAll(mapOf<String, Boolean>(
        "fabric" to loader.equals("fabric"),
        "neoforge" to loader.equals("neoforge"),
        "forge" to loader.equals("forge"),
        "vanilla" to loader.equals("vanilla"),
        "legacy" to (minecraft == "1.20.1"),
        "new_pipeline" to ((minecraft == "1.21.10") || (minecraft == "1.21.8") || (minecraft == "1.21.11") || (minecraft == "26.1.2") || (minecraft == "26.2"))

    ))

    /*swaps["peek_diff"] = when {
        eval(current.version, "=1.21.10") && loader.equals("fabric") -> "guiGraphics.scissorStack.peek()"
        else -> "guiGraphics.peekScissorStack()"
    }
    swaps["identifier_import"] = when {
        eval(current.version, "<1.21.11") -> "ResourceLocation"
        else -> "Identifier"
    }*/

    replacements.string(current.version >= "1.21.10" && loader.equals("fabric")) {
        replace("guiGraphics.peekScissorStack()", "guiGraphics.scissorStack.peek()")
    }

    replacements.string(current.parsed >= "1.21.11") {
        replace("net.minecraft.resources.ResourceLocation", "net.minecraft.resources.Identifier")
        replace("renderer.RenderType", "renderer.rendertype.RenderType")
    }

    replacements.string(current.parsed.eq("26.1.2") || current.parsed.eq("26.2")) {
        replace("net.minecraft.client.gui.GuiGraphics", "net.minecraft.client.gui.GuiGraphicsExtractor")
        replace("GuiGraphics.class", "GuiGraphicsExtractor.class")
        replace("(GuiGraphics guiGraphics)", "(GuiGraphicsExtractor guiGraphics)")
        replace(", GuiGraphics guiGraphics", ", GuiGraphicsExtractor guiGraphics")
        replace("net.minecraft.client.gui.render.state", "net.minecraft.client.renderer.state.gui")
    }

    replacements.regex(current.parsed >= "1.21.11") {
        replace("\\bResourceLocation\\b" to "Identifier", "Identifier" to "ResourceLocation")
    }


}


tasks.named<Copy>("processResources") {
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}
/*
configurations.named("modstitchShadow"){
    isTransitive = false
    isCanBeConsumed = true
}
*/

msShadow {
    relocatePackage.set("${modstitch.metadata.modGroup.get()}.${modstitch.metadata.modId.get()}.relocated")
    dependency("io.vavr:vavr:0.11.0", mapOf("io.vavr" to "io.vavr")){
        exclude("META-INF/")
    }
    dependency("net.neoforged:bus:8.0.5", mapOf("net.neoforged.bus" to "net.neoforged.bus")){
        exclude("org.ow2.asm")

        exclude("net.jodah")
        exclude("org.apache.logging.log4j")
        exclude("cpw.mods", "modlauncher")
    }
    dependency("net.jodah:typetools:0.6.3", mapOf("net.jodah" to "net.jodah"))
}




// All dependencies should be specified through modstitch's proxy configuration.
// Wondering where the "repositories" block is? Go to "stonecutter.gradle.kts"
// If you want to create proxy configurations for more source sets, such as client source sets,
// use the modstitch.createProxyConfigurations(sourceSets["client"]) function.
dependencies {
/*
    val loader = when {
        modstitch.isLoom -> Loaders.LOOM
        modstitch.isModDevGradleLegacy -> Loaders.FORGE
        modstitch.isModDevGradleRegular -> Loaders.NEOFORGE
        else -> throw IllegalArgumentException("unknown loader")
    }
*/

    fun Dependency?.jij() = this?.also(::modstitchJiJ)
    fun String.implementation() = if (modstitch.isModDevGradleLegacy){
        add("modImplementation", this)
    } else {
        modstitchModImplementation(this)
    }
    fun String.runtimeOnly() = if (modstitch.isModDevGradleLegacy) {
        add("modRuntimeOnly", this)
    } else {
        modstitchModRuntimeOnly(this)
    }
    val fzzyConfigVersion = findProperty("deps.fzzy_config_version")
    val fzzyMinecraftVersion = when (minecraft) {
        "1.21.1" -> "1.21"
        "1.21.4" -> "1.21.3"
        "1.21.8" -> "1.21.6"
        "1.21.10" -> "1.21.9"
        "26.1.2" -> "26.1"
        "26.2" -> "26.2"
        else -> minecraft
    }
    var fzzyString : String = "";

    // No 26.2 build of common-networking exists, and its 1.21.11 jar is intermediary-mapped so it
    // can't resolve under 26.2's no-remap Loom flow anyway (see NetworkUtils.java gating). Unused
    // in source for this target, so just omit the mod dependency entirely rather than pull in a
    // "requires commonnetworking" fabric.mod.json entry the user can never satisfy.
    if (minecraft != "26.2") {
        "mysticdrew:common-networking-$loader:${property("deps.common_networking") as String}".runtimeOnly()
        modstitchModCompileOnly ("mysticdrew:common-networking-$loader:${property("deps.common_networking") as String}")
    }

    //fzzy
    modstitch.loom {
        val fabricApi = property("deps.fabric_api") as String
        modstitchModImplementation("net.fabricmc.fabric-api:fabric-api:${fabricApi}+${minecraft}")
        fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+${fzzyMinecraftVersion}";

    }

    modstitch.moddevgradle {
        if (modstitch.isModDevGradleLegacy){
            fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+${fzzyMinecraftVersion}+forge";
        } else {
            if (minecraft == "1.21.8"){
                fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+1.21.7+neoforge";
            } else {
                fzzyString = "me.fzzyhmstrs:fzzy_config:${fzzyConfigVersion}+${fzzyMinecraftVersion}+neoforge"
            }

        }

    }

    modstitchModCompileOnly(fzzyString)
    (fzzyString).runtimeOnly()

    //loader-specified deps
    DependencyConfig.getDependencies(loader, minecraft).forEach { dep ->
        dependencies.add(dep.configuration, dep.notation, dep.options)
    }
    //lombok
    modstitchCompileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")

    testCompileOnly("org.projectlombok:lombok:1.18.42")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.42")

    //shadow dep
    modstitchImplementation("io.vavr:vavr:0.11.0")
    modstitchImplementation("net.neoforged:bus:8.0.5")

    modstitchImplementation("com.google.code.findbugs:jsr305:3.0.2")
}

msPublishing {

    mpp {
        changelog = file("../../changelog.md")
            .readLines()
            .joinToString("\n") { line ->
                if (line.isNotBlank()) {
                    "$line</br>"
                } else {
                    line
                }
            }
        type = STABLE
        //I think this is provided by modstich or stonecutter. So we can't add this otherwise the upload will fail.
        //modLoaders.add(loader)
        //dryRun = true
        val finalFile = modstitch.finalJarTask.map { it.archiveFile.get() }
        file.set(finalFile)
        displayName = file.map { it.asFile.name }
        if (file("D:\\curseforge-key.txt").exists()) {
            val cfOptions = curseforgeOptions {
                accessToken = file("D:\\curseforge-key.txt").readText()
                projectId = "1164411"
                minecraftVersions.add(minecraft)
                requires("fzzy-config", "common-network")
            }
            curseforge("toCurseForge") {
                from(cfOptions)
            }
        }

        if (file("D:\\modrinth-key.txt").exists()) {
            // Modrinth options used by both Fabric and Forge
            val mrOptions = modrinthOptions {
                accessToken = file("D:\\modrinth-key.txt").readText()
                version = "${loader}-${minecraft}-${modstitch.metadata.modVersion.get()}"
                projectId = "6gKEW2ql"
                minecraftVersions.add(minecraft)
                requires("fzzy-config", "common-network")
            }
            modrinth("toModrinth") {
                from(mrOptions)
            }
        }


    }

}
