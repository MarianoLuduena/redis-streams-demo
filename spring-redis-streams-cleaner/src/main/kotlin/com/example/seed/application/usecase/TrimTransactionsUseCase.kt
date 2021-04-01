package com.example.seed.application.usecase

import com.example.seed.application.port.`in`.TrimTransactionsInPort
import com.example.seed.application.port.out.TransactionRepository
import org.springframework.stereotype.Component

@Component
class TrimTransactionsUseCase(
        private val transactionRepository: TransactionRepository
): TrimTransactionsInPort {

    override fun execute(maxLength: Int) {
        transactionRepository.trim(maxLength)
    }

}
