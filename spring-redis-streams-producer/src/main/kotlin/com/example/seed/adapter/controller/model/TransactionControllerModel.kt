package com.example.seed.adapter.controller.model

import com.example.seed.domain.Transaction
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class TransactionControllerModel(
        val id: String,
        val type: String,
        val description: String,
        val clientId: String,
        val accountId: Long,
        val amount: BigDecimal
) {

    companion object {
        fun fromDomain(transaction: Transaction): TransactionControllerModel =
                TransactionControllerModel(
                        id = transaction.id,
                        type = transaction.type,
                        description = transaction.description,
                        clientId = transaction.clientId,
                        accountId = transaction.accountId,
                        amount = transaction.amount
                )
    }

}
