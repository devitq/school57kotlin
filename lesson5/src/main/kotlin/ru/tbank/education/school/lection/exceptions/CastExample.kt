package ru.tbank.education.school.lection.exceptions

fun main() {
  println("Введите целое число: ")
  val input = readLine()

  val number = input?.toInt()
  println("Вы ввели число: $number")
}
