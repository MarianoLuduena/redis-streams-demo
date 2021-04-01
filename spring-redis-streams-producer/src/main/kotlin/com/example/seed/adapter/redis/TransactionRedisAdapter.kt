package com.example.seed.adapter.redis

import com.example.seed.adapter.redis.model.TransactionRedisModel
import com.example.seed.application.port.`in`.CreateTransactionInPort
import com.example.seed.application.port.out.TransactionRepository
import com.example.seed.config.SeedConfig
import com.example.seed.domain.Transaction
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Repository

@Repository
class TransactionRedisAdapter(
        config: SeedConfig,
        private val redisClient: RedisClient
) : TransactionRepository {

    private val streamName: String = config.transactionRepository!!.streamName

    override fun save(cmd: CreateTransactionInPort.CreateTransactionCmd): Transaction {
        val model = TransactionRedisModel.from(cmd)
        val id = redisClient.append(streamName, model)
        return model.toDomain(id)
    }

}
