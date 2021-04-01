package com.example.seed.adapter.redis

import brave.Tracer
import brave.propagation.TraceContext
import brave.propagation.TraceContextOrSamplingFlags
import com.fasterxml.jackson.databind.ObjectMapper
import io.lettuce.core.RedisBusyException
import org.slf4j.LoggerFactory
import org.springframework.data.redis.RedisSystemException
import org.springframework.data.redis.connection.stream.Consumer
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.connection.stream.StreamOffset
import org.springframework.data.redis.connection.stream.ReadOffset
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.data.redis.stream.Subscription
import org.springframework.stereotype.Repository
import java.math.BigInteger
import kotlin.random.Random

@Repository
class RedisClient(
        private val redisTemplate: StringRedisTemplate,
        private val objectMapper: ObjectMapper,
        private val container: StreamMessageListenerContainer<String, StringMapRecord>,
        private val tracer: Tracer
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    fun createConsumerGroup(stream: String, group: String) {
        try {
            log.info("Creating consumer group {} for stream {}", group, stream)
            streamOps().createGroup(stream, ReadOffset.latest(), group)
        } catch (e: RedisSystemException) {
            when (e.rootCause) {
                is RedisBusyException ->
                    log.warn("Consumer group {} already exists for stream {}: {}", group, stream,
                            e.rootCause!!.localizedMessage)
                else -> {
                    log.error("Error creating consumer group {} for stream {}", group, stream, e)
                    throw e
                }
            }
        }
    }

    fun <T> subscribe(
            consumer: Consumer,
            stream: String,
            clazz: Class<T>,
            handler: (String, T) -> Unit
    ): Subscription =
            container.receive(consumer, StreamOffset.create(stream, ReadOffset.lastConsumed())) { msg ->
                val model = readPayload(msg.value.getOrDefault(PAYLOAD_FIELD, ""), clazz)
                val traceContext = readTraceContext(msg)
                val span = tracer.toSpan(TraceContextOrSamplingFlags.create(traceContext).context()).start()
                try {
                    tracer.withSpanInScope(span).use { handler(msg.id.value, model) }
                } catch (e: Throwable) {
                    log.error("Unexpected exception", e)
                } finally {
                    span.finish()
                }
                streamOps().acknowledge(consumer.group, msg)
            }

    private fun readTraceContext(msg: StringMapRecord): TraceContext {
        val default = { java.lang.Long.toHexString(Random.nextLong(1L, Long.MAX_VALUE)) }
        val traceIdHexStr = msg.value.getOrElse(TRACE_ID, default)
        val spanIdHexStr = msg.value.getOrDefault(SPAN_ID, traceIdHexStr)
        return TraceContext.newBuilder()
                .traceId(BigInteger(traceIdHexStr, 16).toLong())
                .spanId(BigInteger(spanIdHexStr, 16).toLong())
                .build()
    }

    private fun <T> readPayload(data: String, clazz: Class<T>): T = objectMapper.readValue(data, clazz)

    private fun streamOps() = redisTemplate.opsForStream<String, StringMapRecord>()

    companion object {
        private const val PAYLOAD_FIELD = "payload"
        private const val TRACE_ID = "X-B3-TraceId"
        private const val SPAN_ID = "X-B3-SpanId"
    }

}

private typealias StringMapRecord = MapRecord<String, String, String>
