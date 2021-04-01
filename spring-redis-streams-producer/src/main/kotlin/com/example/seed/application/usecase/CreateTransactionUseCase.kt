package com.example.seed.application.usecase

import com.example.seed.application.port.`in`.CreateTransactionInPort
import com.example.seed.application.port.out.TransactionRepository
import com.example.seed.domain.Transaction
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture
import java.util.concurrent.Executor

@Component
class CreateTransactionUseCase(
        private val transactionRepository: TransactionRepository,
        @Qualifier("asyncExecutor") private val executor: Executor
) : CreateTransactionInPort {

    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun execute(cmd: CreateTransactionInPort.CreateTransactionCmd): CompletableFuture<Transaction> {
        return CompletableFuture.supplyAsync({
            log.info("Creating new transaction {}", cmd)
            val transaction = transactionRepository.save(cmd)
            log.info("Transaction successfully created {}", transaction)
            transaction
        }, executor)
    }

}
