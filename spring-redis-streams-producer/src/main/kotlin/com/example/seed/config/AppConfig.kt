package com.example.seed.config

import org.springframework.beans.factory.BeanFactory
import org.springframework.cloud.sleuth.instrument.async.LazyTraceExecutor
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.EnableScheduling
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import java.util.concurrent.Executor

@Configuration
@EnableScheduling
class AppConfig(private val traceSleuthInterceptor: TraceSleuthInterceptor,
                private val beanFactory: BeanFactory) : WebMvcConfigurer {

    companion object {
        private const val CORE_POOL_SIZE = 5
        private const val MAX_POOL_SIZE = 20
        private const val ASYNC_PREFIX = "async-"
        private const val WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN = true
    }

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(traceSleuthInterceptor)
    }

    @Bean("asyncExecutor")
    fun getAsyncExecutor(): Executor {
        val executor = ThreadPoolTaskExecutor()
        executor.corePoolSize = CORE_POOL_SIZE
        executor.maxPoolSize = MAX_POOL_SIZE
        executor.setWaitForTasksToCompleteOnShutdown(WAIT_FOR_TASK_TO_COMPLETE_ON_SHUTDOWN)
        executor.setThreadNamePrefix(ASYNC_PREFIX)
        executor.initialize()
        return LazyTraceExecutor(beanFactory, executor)
    }

}
