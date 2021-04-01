package com.example.seed.config

import brave.Tracer
import org.springframework.stereotype.Component
import org.springframework.web.servlet.AsyncHandlerInterceptor
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class TraceSleuthInterceptor(private val tracer: Tracer) : AsyncHandlerInterceptor {
    override fun preHandle(
            request: HttpServletRequest,
            response: HttpServletResponse,
            handler: Any
    ): Boolean {
        val traceId = tracer
                .currentSpan()
                ?.context()
                ?.traceIdString()
                ?: TRACE_ID_MISSING

        if (!response.headerNames.contains(X_B3_TRACE_ID)) {
            response.addHeader(X_B3_TRACE_ID, traceId)
        }

        val spanId = tracer
                .currentSpan()
                ?.context()
                ?.spanIdString()
                ?: SPAN_ID_MISSING

        if (!response.headerNames.contains(X_B3_SPAN_ID)) {
            response.addHeader(X_B3_SPAN_ID, spanId)
        }

        return true
    }

    companion object {
        const val TRACE_ID_MISSING = "Trace id does not exist"
        const val SPAN_ID_MISSING = "Span id does not exist"
        private const val X_B3_TRACE_ID = "X-B3-TraceId"
        private const val X_B3_SPAN_ID = "X-B3-SpanId"
    }
}
