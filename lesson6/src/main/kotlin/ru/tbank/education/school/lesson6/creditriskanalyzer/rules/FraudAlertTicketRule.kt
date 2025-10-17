package ru.tbank.education.school.lesson6.creditriskanalyzer.rules

import ru.tbank.education.school.lesson6.creditriskanalyzer.models.Client
import ru.tbank.education.school.lesson6.creditriskanalyzer.models.PaymentRisk
import ru.tbank.education.school.lesson6.creditriskanalyzer.models.ScoringResult
import ru.tbank.education.school.lesson6.creditriskanalyzer.models.TicketTopic
import ru.tbank.education.school.lesson6.creditriskanalyzer.repositories.TicketRepository

/**
 * Проверяет, были ли у клиента обращения, связанные с мошенничеством.
 *
 * Идея:
 * - Получить все тикеты клиента.
 * - Проверить, есть ли обращения с темой FRAUD_ALERT.
 * - Оценить, решены ли такие тикеты.
 *
 * Как считать score:
 * - Если есть FRAUD_ALERT и он не решён → HIGH
 * - Если FRAUD_ALERT есть, но решён → MEDIUM
 * - Если FRAUD_ALERT нет вообще → LOW
 *
 */
class FraudAlertTicketRule(
    private val ticketRepo: TicketRepository
) : ScoringRule {

    override val ruleName: String = "Fraud Alert Ticket"

    override fun evaluate(client: Client): ScoringResult {
        val tickets = ticketRepo.getTickets(client.id)

        var hasFraudAlert = false
        var unresolvedFraudAlert = false

        for (ticket in tickets) {
            if (ticket.topic == TicketTopic.FRAUD_ALERT) {
                hasFraudAlert = true
                if (!ticket.resolved) {
                    unresolvedFraudAlert = true
                    break
                }
            }
        }

        val risk = when {
            unresolvedFraudAlert -> PaymentRisk.HIGH
            hasFraudAlert -> PaymentRisk.MEDIUM
            else -> PaymentRisk.LOW
        }

        return ScoringResult(ruleName, risk)
    }
}