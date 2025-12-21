package ru.tbank.education.school.lesson8.homework.payments

import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class PaymentProcessorTest {
    private lateinit var processor: PaymentProcessor
    private val validAmount = 1
    private val validCardNumber = "4242424242424242"
    private val validExpiryMonth = 1
    private val validExpiryYear = 2026
    private val validCurrency = "USD"
    private val validCustomerId = "42"

    @BeforeEach
    fun setUp() {
        processor = PaymentProcessor()
    }

    @ParameterizedTest
    @CsvSource(
        "0",
        "-100"
    )
    fun `amount validation`(amount: Int) {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(amount, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, validCustomerId)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "''",
        "1234",
        "12345678901234567890",
        "s0m3l0ngt3xt42",
    )
    fun `card number validation`(cardNumber: String) {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(validAmount, cardNumber, validExpiryMonth, validExpiryYear, validCurrency, validCustomerId)
        }
    }

    @ParameterizedTest
    @CsvSource(
        "0, 2025, true",
        "13, 2025, true",
        "-1, 2025, true",
        "10, 2024, true",
        "12, 2024, true",
        "-10, 2020, true",
        "10, 2025, true",
        "13, 2026, true",
        "-10, 2026, true",

        "11, 2025, false",
        "12, 2025, false",
        "1, 2026, false",
        "12, 2026, false"
    )
    fun `expiry validation`(month: Int, year: Int, shouldRaise: Boolean) {
        if (shouldRaise) {
            assertThrows(IllegalArgumentException::class.java) {
                processor.processPayment(validAmount, validCardNumber, month, year, validCurrency, validCustomerId)
            }
        } else {
            assertDoesNotThrow {
                processor.processPayment(validAmount, validCardNumber, month, year, validCurrency, validCustomerId)
            }
        }
    }

    @Test
    fun `currency validation`() {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(validAmount, validCardNumber, validExpiryMonth, validExpiryYear, "", validCustomerId)
        }
    }

    @Test
    fun `customerId validation`() {
        assertThrows(IllegalArgumentException::class.java) {
            processor.processPayment(validAmount, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "")
        }
    }

    @ParameterizedTest
    @CsvSource(
        "4444123412341234",
        "5555123412341234",
        "1111123412341234",
        "9999123412341234",

        "4242424242424241",
        "4012888888881882",
        "4000000000000000",
        "4532015112830360",
        "4485275742308320",
        "4716108999716530",
        "5555555555554440",
        "5105105105105101",
        "5200828282828211",
        "5399999999999990",
        "378282246310000",
        "371449635398430",
        "30569309025900",
        "6011111111111110",
        "6011000990139420",
        "3530111333300001",
        "3566002020360500",
        "7992739871320000"
    )
    fun `suspicious card`(cardNumber: String) {
        val result = processor.processPayment(validAmount, cardNumber, validExpiryMonth, validExpiryYear, validCurrency, validCustomerId)

        assertEquals("REJECTED", result.status)
        assertEquals("Payment blocked due to suspected fraud", result.message)
    }

    @ParameterizedTest
    @CsvSource(
        "USD, 100, 100",
        "usd, 100, 100",
        "EUR, 100, 92",
        "eur, 100, 92",
        "GBP, 100, 78",
        "gbp, 100, 78",
        "gbP, 100, 78",
        "JPY, 100, 15000",
        "jpy, 100, 15000",
        "RUB, 100, 9000",
        "rub, 100, 9000",
        "ABC, 100, 100"
    )
    fun `currency normalization and conversion`(inputCurrency: String, amount: Int, expected: Int) {
        val result = processor.processPayment(
            amount,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            inputCurrency,
            validCustomerId
        )

        assertEquals("SUCCESS", result.status)
    }

    @Test
    fun `gateway limit exceeded`() {
        val result = processor.processPayment(
            200_000,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )

        assertEquals("FAILED", result.status)
        assertEquals("Transaction limit exceeded", result.message)
    }

    @Test
    fun `gateway card blocked`() {
        val result = processor.processPayment(
            100,
            "4445000000000005",
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )

        assertEquals("FAILED", result.status)
        assertEquals("Card is blocked", result.message)
    }

    @Test
    fun `gateway insufficient funds`() {
        val result = processor.processPayment(
            100,
            "5500000000000004",
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )

        assertEquals("FAILED", result.status)
        assertEquals("Insufficient funds", result.message)
    }

    @Test
    fun `gateway timeout simulated by amount divisible by 17`() {
        val result = processor.processPayment(
            17,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )

        assertEquals("FAILED", result.status)
        assertEquals("Gateway timeout", result.message)
    }

    @Test
    fun `successful payment`() {
        val result = processor.processPayment(
            100,
            validCardNumber,
            validExpiryMonth,
            validExpiryYear,
            validCurrency,
            validCustomerId
        )

        assertEquals("SUCCESS", result.status)
        assertEquals("Payment completed", result.message)
    }

    @ParameterizedTest
    @CsvSource(
        "0, 1000, 0",
        "300, 1000, 0",
        "500, 1000, 50",
        "2000, 1000, 100",
        "5000, 1000, 150",
        "10000, 1000, 200",
        "50000, 100000, 5000"
    )
    fun `loyalty discount calculation`(points: Int, base: Int, expected: Int) {
        val discount = processor.calculateLoyaltyDiscount(points, base)
        assertEquals(expected, discount)
    }

    @Test
    fun `discount invalid base amount`() {
        assertThrows(IllegalArgumentException::class.java) {
            processor.calculateLoyaltyDiscount(1000, 0)
        }
    }

    @Test
    fun `bulk process empty`() {
        val result = processor.bulkProcess(emptyList())
        assertTrue(result.isEmpty())
    }

    @Test
    fun `bulk process all success`() {
        val payments = listOf(
            PaymentData(10, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "1"),
            PaymentData(20, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "2"),
            PaymentData(30, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency,  "3")
        )
        val results = processor.bulkProcess(payments)

        assertEquals(3, results.size)
        assertTrue(results.all { it.status == "SUCCESS" })
    }

    @Test
    fun `bulk process with invalid data`() {
        val payments = listOf(
            PaymentData(10, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "1"),
            PaymentData(0, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "2"),
            PaymentData(30, validCardNumber, validExpiryMonth, validExpiryYear, validCurrency, "3")
        )
        val results = processor.bulkProcess(payments)

        assertEquals(3, results.size)
        assertEquals("REJECTED", results[1].status)
        assertEquals("Amount must be positive", results[1].message)
    }
}