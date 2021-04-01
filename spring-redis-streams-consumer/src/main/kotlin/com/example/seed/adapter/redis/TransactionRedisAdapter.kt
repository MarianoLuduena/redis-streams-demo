package com.example.seed.adapter.redis

import com.example.seed.adapter.redis.model.TransactionRedisModel
import com.example.seed.application.port.`in`.ProcessTransactionInPort
import com.example.seed.config.SeedConfig
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.stream.Subscription
import org.springframework.stereotype.Repository

@Repository
class TransactionRedisAdapter(
        config: SeedConfig,
        private val redisClient: RedisClient,
        private val processTransactionInPort: ProcessTransactionInPort
) {

    private val streamName: String = config.transactionRepository!!.streamName
    private val consumerGroup: String = config.transactionRepository!!.consumerGroup
    private val consumerId: String = config.transactionRepository!!.consumerId

    private val log = LoggerFactory.getLogger(this.javaClass)

    private val subscription: Subscription =
            redisClient.createConsumerGroup(streamName, consumerGroup)
                    .let {
                        redisClient.subscribe(
                                Consumer.from(consumerGroup, consumerId), streamName,
                                TransactionRedisModel::class.java
                        ) { id, model ->
                            log.info("Received message with [id: {}] {} from stream {} / {} / {}",
                                    id, model, streamName, consumerGroup, consumerId)
                            processTransactionInPort.execute(model.toDomain(id))
                        }
                    }

}
