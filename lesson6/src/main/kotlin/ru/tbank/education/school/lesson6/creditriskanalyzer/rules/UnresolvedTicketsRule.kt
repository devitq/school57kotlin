package ru.tbank.education.school.lesson6.creditriskanalyzer.rules

import ru.tbank.education.school.lesson6.creditriskanalyzer.models.Client
import ru.tbank.education.school.lesson6.creditriskanalyzer.models.PaymentRisk
import ru.tbank.education.school.lesson6.creditriskanalyzer.models.ScoringResult
import ru.tbank.education.school.lesson6.creditriskanalyzer.repositories.TicketRepository

/**
 * Проверяет, насколько часто клиент имеет нерешённые обращения в поддержку.
 *
 * Идея:
 * - Получить все тикеты клиента.
 * - Определить количество нерешённых обращений.
 * - Рассчитать долю нерешённых тикетов.
 *
 * Как считать risk:
 * - Если доля > 0.5 → HIGH
 * - Если доля > 0.2 → MEDIUM
 * - Иначе → LOW
 */
class UnresolvedTicketsRule(
    private val ticketRepo: TicketRepository
) : ScoringRule {

    override val ruleName: String = "Unresolved Tickets"

    override fun evaluate(client: Client): ScoringResult {
        val tickets = ticketRepo.getTickets(client.id)

        var unsolvedTicketsCount = 0
        val totalTicketsCount = tickets.size

        for (ticket in tickets) {
            if (!ticket.resolved) {
                unsolvedTicketsCount++
            }
        }

        val ratio = if (totalTicketsCount > 0) unsolvedTicketsCount.toDouble() / totalTicketsCount else 0.0

        val risk = when {
            ratio > 0.5 -> PaymentRisk.HIGH
            ratio > 0.2 -> PaymentRisk.MEDIUM
            else -> PaymentRisk.LOW
        }

        return ScoringResult(ruleName, risk)
    }
}
