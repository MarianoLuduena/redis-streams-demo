package com.example.seed.adapter.controller.model

import com.example.seed.application.port.`in`.CreateTransactionInPort
import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.PropertyNamingStrategy
import com.fasterxml.jackson.databind.annotation.JsonNaming
import java.math.BigDecimal

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategy.SnakeCaseStrategy::class)
data class CreateTransactionControllerModel(
        val type: String,
        val description: String,
        val clientId: String,
        val accountId: Long,
        val amount: BigDecimal
) {

    fun toDomain(): CreateTransactionInPort.CreateTransactionCmd =
            CreateTransactionInPort.CreateTransactionCmd(
                    type = type,
                    description = description,
                    clientId = clientId,
                    accountId = accountId,
                    amount = amount
            )

}
