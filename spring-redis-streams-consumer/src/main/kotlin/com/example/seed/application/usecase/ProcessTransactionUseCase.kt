package com.example.seed.application.usecase

import com.example.seed.application.port.`in`.ProcessTransactionInPort
import com.example.seed.config.SeedConfig
import com.example.seed.domain.Transaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class ProcessTransactionUseCase(config: SeedConfig) : ProcessTransactionInPort {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val workDelay = config.processTransaction!!.maxWorkDelay

    override fun execute(trx: Transaction): Transaction {
        val startTime = System.currentTimeMillis()
        log.info("Starting processing of transaction {}", trx)
        val delay = Random.nextInt(0, workDelay)
        if (delay > 0) Thread.sleep(delay.toLong())
        // DO SOMETHING
        val endTime = System.currentTimeMillis()
        log.info("Finished processing of transaction {} in {} milliseconds", trx, endTime - startTime)
        return trx
    }

}
