package main.kotlin.ru.tbank.education.school.lesson9

import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

fun createZipFromDirectory(
    sourceDir: File,
    targetZip: File,
    allowedExtensions: Set<String> = setOf("txt", "log")
) {
    require(sourceDir.exists() && sourceDir.isDirectory) {
        "Source directory must exist and be a directory: ${sourceDir.path}"
    }

    targetZip.parentFile?.let { parent ->
        if (!parent.exists()) parent.mkdirs()
    }

    try {
        FileOutputStream(targetZip).use { fos ->
            ZipOutputStream(fos).use { zos ->
                sourceDir.walkTopDown()
                    .filter { it.isFile }
                    .forEach { file ->
                        val ext = file.extension.lowercase()
                        if (ext !in allowedExtensions) return@forEach

                        val relPath = sourceDir.toPath()
                            .relativize(file.toPath())
                            .toString()
                            .replace(File.separatorChar, '/')

                        println("$relPath - ${file.length()} bytes")

                        FileInputStream(file).use { fis ->
                            val entry = ZipEntry(relPath)
                            zos.putNextEntry(entry)
                            fis.copyTo(zos, DEFAULT_BUFFER_SIZE)
                            zos.closeEntry()
                        }
                    }
            }
        }
    } catch (ex: Exception) {
        System.err.println("Archive creation failed: ${ex.message}")
        throw ex
    }
}

fun main() {
    val src = File("lesson9")
    val out = File("archive.zip")
    try {
        createZipFromDirectory(src, out)
        println("Archive created: ${out.absolutePath}")
    } catch (e: Exception) {
        System.err.println("Failed: ${e.message}")
    }
}
