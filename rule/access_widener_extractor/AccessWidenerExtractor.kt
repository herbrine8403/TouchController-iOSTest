package top.fifthlight.fabazel.accesswidenerextractor

import kotlinx.serialization.json.Json
import net.fabricmc.accesswidener.AccessWidenerReader
import net.fabricmc.accesswidener.AccessWidenerWriter
import net.fabricmc.accesswidener.TransitiveOnlyFilter
import top.fifthlight.bazel.worker.api.Worker
import java.io.PrintWriter
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.writer

fun main(vararg args: String) = AccessWidenerExtractor.run(*args)

object AccessWidenerExtractor : Worker() {
    override fun handleRequest(
        out: PrintWriter,
        sandboxDir: Path,
        args: Array<String>,
    ): Int {
        try {
            if (args.isEmpty()) {
                out.println("Usage: AccessWidenerExtractor <output> [inputJars...]")
                return 1
            }

            val outputPath = sandboxDir.resolve(Path.of(args[0]))
            val format = Json {
                ignoreUnknownKeys = true
            }
            val writer = AccessWidenerWriter()
            for (index in 1 until args.size) {
                val arg = args[index]
                try {
                    val jarPath = sandboxDir.resolve(Path.of(arg))
                    JarFile(jarPath.toFile(), false).use { jarFile ->
                        val jsonEntry = jarFile.getJarEntry("fabric.mod.json") ?: continue
                        val json = jarFile.getInputStream(jsonEntry).reader()
                            .use { format.decodeFromString<FabricModJson>(it.readText()) }
                        val accessWidenerPath = json.accessWidener ?: continue
                        val accessWidenerEntry = jarFile.getJarEntry(accessWidenerPath)
                            ?: error("Bad access widener path: $accessWidenerPath")
                        val reader = AccessWidenerReader(TransitiveOnlyFilter(writer))
                        jarFile.getInputStream(accessWidenerEntry).use { reader.read(it.bufferedReader()) }
                    }
                } catch (ex: Exception) {
                    throw RuntimeException("Failed when processing $arg", ex)
                }
            }
            outputPath.writer().use {
                it.write(writer.writeString())
            }
            return 0
        } catch (ex: Exception) {
            ex.printStackTrace(out)
            return 1
        }
    }
}
