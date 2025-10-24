package ru.tbank.education.school

import ru.tbank.education.school.homework.FileAnalyzer
import ru.tbank.education.school.homework.IOFileAnalyzer

fun main() {
  val analyzer: FileAnalyzer = IOFileAnalyzer()
  analyzer.countLinesAndWordsInFile(
      "./lesson5/src/main/kotlin/ru/tbank/education/school/homework/samples/input.txt",
      "./lesson5/src/main/kotlin/ru/tbank/education/school/homework/samples/output.txt",
  )
}
