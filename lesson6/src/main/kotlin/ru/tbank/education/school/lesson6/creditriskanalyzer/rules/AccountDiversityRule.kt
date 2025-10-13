package ru.tbank.education.school.lesson6.creditriskanalyzer.rules

import ru.tbank.education.school.lesson6.creditriskanalyzer.models.*
import ru.tbank.education.school.lesson6.creditriskanalyzer.repositories.AccountRepository

/**
 * Проверяет разнообразие счетов клиента по типам и валютам.
 *
 * Идея:
 * - Получить все счета клиента.
 * - Посчитать количество уникальных типов счетов.
 * - Посчитать количество уникальных валют.
 * - Суммировать эти показатели для определения диверсификации.
 *
 * Как считать risk:
 * - Если итоговое значение <= 2 → HIGH
 * - Если итоговое значение <= 4 → MEDIUM
 * - Если > 4 → LOW
 */
class AccountDiversityRule(
    private val accountRepo: AccountRepository
) : ScoringRule {

    override val ruleName: String = "Account Diversity"

    override fun evaluate(client: Client): ScoringResult {
        val clientAccounts = accountRepo.getAccounts(client.id)

        val uniqueAccountTypes = mutableSetOf<AccountType>();
        val uniqueAccountCurrencies = mutableSetOf<Currency>();

        for (account in clientAccounts) {
            uniqueAccountTypes.add(account.type)
            uniqueAccountCurrencies.add(account.currency)
        }

        val uniqueCount = uniqueAccountTypes.size + uniqueAccountCurrencies.size

        val risk = when {
            uniqueCount <= 2 -> PaymentRisk.HIGH
            uniqueCount <= 4 -> PaymentRisk.MEDIUM
            else -> PaymentRisk.LOW
        }
        return ScoringResult(this.ruleName, risk)
    }
}
