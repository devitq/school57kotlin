package ru.tbank.education.school.homework

/** Исключение, которое выбрасывается при попытке забронировать занятое место */
class SeatAlreadyBookedException(message: String) : Exception(message)

/**
 * Исключение, которое выбрасывается при попытке забронировать место при отсутствии свободных мест
 */
class NoAvailableSeatException(message: String) : Exception(message)

data class BookedSeat(
    val movieId: String, // идентификатор фильма
    val seat: Int, // номер места
)

class MovieBookingService(
    private val maxQuantityOfSeats: Int // Максимальное кол-во мест
) {
  private val books = mutableSetOf<BookedSeat>()

  init {
    require(maxQuantityOfSeats > 0) { "maxQuantityOfSeats must be > 0" }
  }

  /**
   * Бронирует указанное место для фильма.
   *
   * @param movieId идентификатор фильма
   * @param seat номер места
   * @throws IllegalArgumentException если номер места вне допустимого диапазона
   * @throws NoAvailableSeatException если нет больше свободных мест
   * @throws SeatAlreadyBookedException если место уже забронировано
   */
  fun bookSeat(movieId: String, seat: Int) {
    require(seat in 1..this.maxQuantityOfSeats)
    if (this.books.count { it.movieId == movieId } >= maxQuantityOfSeats) {
      throw NoAvailableSeatException("No books available for $movieId")
    }
    if (this.isSeatBooked(movieId, seat)) {
      throw SeatAlreadyBookedException("Seat $seat is already booked for $movieId")
    }
    this.books.add(BookedSeat(movieId, seat))
  }

  /**
   * Отменяет бронь указанного места.
   *
   * @param movieId идентификатор фильма
   * @param seat номер места
   * @throws NoSuchElementException если место не было забронировано
   */
  fun cancelBooking(movieId: String, seat: Int) {
    if (!this.isSeatBooked(movieId, seat)) {
      throw NoSuchElementException("Seat $seat has not been booked yet for $movieId")
    }
    this.books.remove(BookedSeat(movieId, seat))
  }

  /**
   * Проверяет, забронировано ли место
   *
   * @return true если место занято, false иначе
   */
  fun isSeatBooked(movieId: String, seat: Int): Boolean {
    return this.books.any { it.movieId == movieId && it.seat == seat }
  }
}
