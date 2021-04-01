package com.example.seed.adapter.redis

import com.example.seed.application.port.out.TransactionRepository
import com.example.seed.config.SeedConfig
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository

@Repository
class TransactionRedisAdapter(
        config: SeedConfig,
        private val redisTemplate: StringRedisTemplate
) : TransactionRepository {

    private val streamName: String = config.transactionRepository!!.streamName
    private val log = LoggerFactory.getLogger(this.javaClass)

    override fun trim(maxLength: Int) {
        log.info("Trimming stream {} to {} elements", streamName, maxLength)
        val ops = streamOps()
        val info = ops.info(streamName)
        log.info("Stream {} info: {}", streamName, info)
        val removedEntries = ops.trim(streamName, maxLength.toLong(), true)
        log.info("Trimmed {} elements from stream {}", removedEntries, streamName)
    }

    private fun streamOps() = redisTemplate.opsForStream<String, StringMapRecord>()

}

private typealias StringMapRecord = MapRecord<String, String, String>
