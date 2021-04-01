package com.example.seed.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import javax.validation.constraints.PositiveOrZero
import kotlin.properties.Delegates

@Validated
@Component
@ConfigurationProperties(prefix = "spring-redis-streams-consumer")
class SeedConfig {

    var transactionRepository: TransactionRepository? = null
    var processTransaction: ProcessTransactionConfig? = null

    class TransactionRepository {
        @get:NotBlank
        lateinit var streamName: String
        @get:NotBlank
        lateinit var consumerGroup: String
        @get:NotBlank
        lateinit var consumerId: String
    }

    class ProcessTransactionConfig {
        @get:PositiveOrZero
        var maxWorkDelay by Delegates.notNull<Int>()
    }

}
