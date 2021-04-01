package com.example.seed.adapter.redis

import brave.Tracer
import com.example.seed.extensions.toIsoString
import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.StreamRecords
import org.springframework.data.redis.connection.stream.StringRecord
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Repository
import java.time.ZoneOffset
import java.time.ZonedDateTime

@Repository
class RedisClient(
        private val redisTemplate: StringRedisTemplate,
        private val objectMapper: ObjectMapper,
        private val tracer: Tracer
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun <T> append(stream: String, value: T): String {
        log.info("Sending message {} with key {} to stream {}", value, PAYLOAD_FIELD, stream)
        val record = buildMessage(stream, value)
        val recordId = streamOps().add(record)
        log.info("Got recordId {}", recordId)
        return recordId?.value ?: ""
    }

    private fun <T> buildMessage(stream: String, value: T): StringRecord {
        val valueStr = objectMapper.writeValueAsString(value)
        val traceId = tracer.currentSpan()?.context()?.traceIdString()!!
        val spanId = tracer.nextSpan()?.context()?.spanIdString()!!
        val map = mapOf(
                Pair(PAYLOAD_FIELD, valueStr),
                Pair(TIMESTAMP_FIELD, currentTimestamp()),
                Pair(TRACE_ID, traceId),
                Pair(SPAN_ID, spanId)
        )
        return StreamRecords.string(map).withStreamKey(stream)
    }

    private fun streamOps() = redisTemplate.opsForStream<String, StringMapRecord>()

    companion object {
        private const val PAYLOAD_FIELD = "payload"
        private const val TIMESTAMP_FIELD = "timestamp"
        private const val TRACE_ID = "X-B3-TraceId"
        private const val SPAN_ID = "X-B3-SpanId"

        private fun currentTimestamp(): String =
                ZonedDateTime.now().withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime().toIsoString()
    }

}

private typealias StringMapRecord = MapRecord<String, String, String>
