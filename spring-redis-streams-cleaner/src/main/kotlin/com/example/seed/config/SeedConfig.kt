package com.example.seed.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank
import kotlin.properties.Delegates

@Validated
@Component
@ConfigurationProperties(prefix = "spring-redis-streams-cleaner")
class SeedConfig {

    var transactionRepository: TransactionRepository? = null
    var transactionScheduler: TransactionScheduler? = null

    class TransactionRepository {
        @get:NotBlank
        lateinit var streamName: String
    }

    class TransactionScheduler {
        var maxLength by Delegates.notNull<Int>()
    }

}
