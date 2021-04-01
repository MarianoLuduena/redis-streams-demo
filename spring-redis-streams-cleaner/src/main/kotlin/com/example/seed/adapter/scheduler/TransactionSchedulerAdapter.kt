package com.example.seed.adapter.scheduler

import com.example.seed.application.port.`in`.TrimTransactionsInPort
import com.example.seed.config.SeedConfig
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component

@Component
class TransactionSchedulerAdapter(
        config: SeedConfig,
        private val trimTransactionsInPort: TrimTransactionsInPort
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val maxLength: Int = config.transactionScheduler!!.maxLength

    @Scheduled(cron = "\${spring-redis-streams-cleaner.transaction-scheduler.cron}")
    fun execute() {
        try {
            doExecute()
        } catch (e: Throwable) {
            log.error("Error while removing old transactions", e)
        }
    }

    private fun doExecute() {
        log.info("Scheduled task started: ready to clean up old transactions")
        trimTransactionsInPort.execute(maxLength)
        log.info("Scheduled task completed")
    }

}