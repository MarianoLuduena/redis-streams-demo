package com.example.seed.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component
import org.springframework.validation.annotation.Validated
import javax.validation.constraints.NotBlank

@Validated
@Component
@ConfigurationProperties(prefix = "spring-redis-streams-producer")
class SeedConfig {

    var transactionRepository: TransactionRepository? = null

    class TransactionRepository {
        @get:NotBlank
        lateinit var streamName: String
    }

}
