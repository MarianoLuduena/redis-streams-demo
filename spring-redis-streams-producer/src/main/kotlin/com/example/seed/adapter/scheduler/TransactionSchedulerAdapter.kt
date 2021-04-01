package com.example.seed.adapter.scheduler

import com.example.seed.application.port.`in`.CreateTransactionInPort
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import kotlin.random.Random

@Component
class TransactionSchedulerAdapter(
        private val createTransactionInPort: CreateTransactionInPort
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(cron = "\${spring-redis-streams-producer.transaction-scheduler.cron}")
    fun execute() {
        try {
            doExecute()
        } catch (e: Throwable) {
            log.error("Error producing new transaction", e)
        }
    }

    private fun doExecute() {
        log.info("Scheduled task started: creating new transaction")
        createTransactionInPort.execute(buildCmd())
        log.info("Scheduled task completed")
    }

    private fun buildCmd(): CreateTransactionInPort.CreateTransactionCmd =
            CreateTransactionInPort.CreateTransactionCmd(
                    type = "02215",
                    description = "TRANSFER",
                    clientId = Random.nextInt(0, Int.MAX_VALUE).toString(),
                    accountId = Random.nextLong(0L, Long.MAX_VALUE),
                    amount = Random.nextDouble(1.0, 10000.0).toBigDecimal()
            )

}
