package com.example.seed.application.port.`in`

import com.example.seed.domain.Transaction
import java.math.BigDecimal
import java.util.concurrent.CompletableFuture

interface CreateTransactionInPort {

    fun execute(cmd: CreateTransactionCmd): CompletableFuture<Transaction>

    data class CreateTransactionCmd(
            val type: String,
            val description: String,
            val clientId: String,
            val accountId: Long,
            val amount: BigDecimal
    )
}
