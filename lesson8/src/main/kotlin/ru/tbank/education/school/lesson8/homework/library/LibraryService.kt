package ru.tbank.education.school.lesson8.homework.library

class LibraryService {
    private val books = mutableMapOf<String, Book>()
    private val borrowedBooks = mutableSetOf<String>()
    private val borrowerFines = mutableMapOf<String, Int>()

    fun addBook(book: Book) {
        books[book.isbn] = book
    }

    fun borrowBook(isbn: String, borrower: String, daysOverdue : Int = 0) {
        if (!books.containsKey(isbn)) {
            throw IllegalArgumentException("this book does not exist")
        }
        if (borrowedBooks.contains(isbn)) {
            throw IllegalArgumentException("this book is not available for borrowing")
        }
        if (hasOutstandingFines(borrower)) {
            throw IllegalArgumentException("borrower has outstanding fines")
        }

        borrowedBooks.add(isbn)
        borrowerFines[borrower] = (borrowerFines[borrower] ?: 0) + calculateOverdueFine(isbn, daysOverdue)
    }

    fun returnBook(isbn: String) {
        if (!borrowedBooks.contains(isbn)) {
            throw IllegalArgumentException("the book is already returned / not has been borrowed yet")
        }

        borrowedBooks.remove(isbn)
    }

    fun isAvailable(isbn: String): Boolean {
        return !borrowedBooks.contains(isbn)
    }

    fun calculateOverdueFine(isbn: String, daysOverdue: Int): Int {
        if (!borrowedBooks.contains(isbn) || daysOverdue < 10) {
            return 0
        }

        return (daysOverdue - 10) * 60
    }

    private fun hasOutstandingFines(borrower: String): Boolean {
        return (borrowerFines[borrower] ?: 0) > 0
    }
}