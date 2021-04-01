package com.example.seed.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.stream.MapRecord
import org.springframework.data.redis.stream.StreamMessageListenerContainer
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.time.Duration

@Configuration
@EnableScheduling
class AppConfig(private val traceSleuthInterceptor: TraceSleuthInterceptor) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(traceSleuthInterceptor)
    }

    @Bean
    fun getStreamListenerContainer(connectionFactory: RedisConnectionFactory)
            : StreamMessageListenerContainer<String, MapRecord<String, String, String>> {
        val containerOptions =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions.builder()
                        .pollTimeout(Duration.ofMillis(100L))
                        .build()

        val container = StreamMessageListenerContainer.create(connectionFactory, containerOptions)
        container.start()
        return container
    }

}
