package ru.tbank.education.school.homework

import java.io.File
import java.io.IOException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths

/**
 * Интерфейс для подсчёта строк и слов в файле.
 */
interface FileAnalyzer {

    /**
     * Считает количество строк и слов в указанном входном файле и записывает результат в выходной файл.
     *
     * Словом считается последовательность символов, разделённая пробелами,
     * табуляциями или знаками перевода строки. Пустые части после разделения не считаются словами.
     *
     * @param inputFilePath путь к входному текстовому файлу
     * @param outputFilePath путь к выходному файлу, в который будет записан результат
     * @return true если операция успешна, иначе false
     */
    fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean
}

class IOFileAnalyzer : FileAnalyzer {
    override fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean {
        return try {
            val inputFile = File(inputFilePath)
            val outputFile = File(outputFilePath)

            if (!inputFile.exists() || !inputFile.isFile) {
                println("Ошибка: входной файл не найден.")
                return false
            }

            val lines = inputFile.readLines()
            val lineCount = lines.size
            var wordCount = 0
            for (line in lines) {
                val words = line.trim().split(" ", "\t")
                wordCount += words.count { it.isNotEmpty() }
            }

            outputFile.writeText("Общее количество строк: $lineCount\nОбщее количество слов: $wordCount\n")

            true
        } catch (e: IOException) {
            println("Ошибка работы с файлом: ${e.message}")
            false
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            false
        }
    }
}

class NIOFileAnalyzer : FileAnalyzer {
    override fun countLinesAndWordsInFile(inputFilePath: String, outputFilePath: String): Boolean {
        return try {
            val inputPath: Path = Paths.get(inputFilePath)
            val outputPath: Path = Paths.get(outputFilePath)

            if (!Files.exists(inputPath)) {
                println("Ошибка: входной файл не найден.")
                return false
            }

            val lines = Files.readAllLines(inputPath)
            val lineCount = lines.size
            var wordCount = 0
            for (line in lines) {
                val words = line.trim().split(" ", "\t")
                wordCount += words.count { it.isNotEmpty() }
            }

            val outputText = "Общее количество строк: $lineCount\nОбщее количество слов: $wordCount\n"
            Files.write(outputPath, outputText.toByteArray())

            true
        } catch (e: IOException) {
            println("Ошибка работы с файлом: ${e.message}")
            false
        } catch (e: Exception) {
            println("Ошибка: ${e.message}")
            false
        }
    }
}