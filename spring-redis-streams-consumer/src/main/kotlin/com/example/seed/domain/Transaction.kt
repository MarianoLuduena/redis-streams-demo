package com.example.seed.domain

import java.math.BigDecimal

data class Transaction(
        val id: String,
        val type: String,
        val description: String,
        val clientId: String,
        val accountId: Long,
        val amount: BigDecimal
)
