package com.example.seed.adapter.redis.model

import com.example.seed.application.port.`in`.CreateTransactionInPort
import com.example.seed.domain.Transaction
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class TransactionRedisModel(
        val type: String,
        val description: String,
        val clientId: String,
        val accountId: Long,
        val amount: BigDecimal
) {

    fun toDomain(id: String): Transaction =
            Transaction(
                    id = id,
                    type = type,
                    description = description,
                    clientId = clientId,
                    accountId = accountId,
                    amount = amount
            )

    companion object {
        fun from(cmd: CreateTransactionInPort.CreateTransactionCmd): TransactionRedisModel =
                TransactionRedisModel(
                        type = cmd.type,
                        description = cmd.description,
                        clientId = cmd.clientId,
                        accountId = cmd.accountId,
                        amount = cmd.amount
                )
    }

}
