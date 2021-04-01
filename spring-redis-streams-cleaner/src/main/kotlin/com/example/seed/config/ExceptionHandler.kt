package com.example.seed.config

import brave.Tracer
import com.example.seed.config.exception.GenericException
import com.example.seed.extensions.toIsoString
import com.fasterxml.jackson.annotation.JsonProperty
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import java.time.LocalDateTime
import java.time.ZoneOffset
import javax.servlet.http.HttpServletRequest

@ControllerAdvice
class ExceptionHandler(
        private val httpServletRequest: HttpServletRequest,
        private val tracer: Tracer?
) {

    private val log = LoggerFactory.getLogger(ExceptionHandler::class.java)

    @ExceptionHandler(Throwable::class)
    fun handle(ex: Throwable): ResponseEntity<ApiErrorResponse> {
        log.error(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, ex)
        return buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex, SPError.INTERNAL_ERROR.errorCode)
    }

    @ExceptionHandler(GenericException::class)
    fun handle(ex: GenericException): ResponseEntity<ApiErrorResponse> {
        log.error(HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase, ex)
        return buildResponseError(HttpStatus.INTERNAL_SERVER_ERROR, ex, ex.errorCode)
    }

    private fun buildResponseError(
            httpStatus: HttpStatus,
            ex: Throwable,
            errorCode: Int,
            errorMessage: String = ex.message ?: ""): ResponseEntity<ApiErrorResponse> {

        val traceId = tracer
                ?.currentSpan()
                ?.context()
                ?.traceIdString()
                ?: TraceSleuthInterceptor.TRACE_ID_MISSING

        val spanId = tracer
                ?.currentSpan()
                ?.context()
                ?.spanIdString()
                ?: TraceSleuthInterceptor.SPAN_ID_MISSING

        val apiErrorResponse = ApiErrorResponse(
                timestamp = LocalDateTime.now(ZoneOffset.UTC).toIsoString(),
                name = httpStatus.reasonPhrase,
                detail = errorMessage,
                status = httpStatus.value(),
                code = errorCode,
                resource = httpServletRequest.requestURI,
                metadata = Metadata(xB3TraceId = traceId, xB3SpanId = spanId)
        )

        return ResponseEntity(apiErrorResponse, httpStatus)
    }

    data class Metadata(
            @JsonProperty(value = "x_b3_trace_id") val xB3TraceId: String,
            @JsonProperty(value = "x_b3_span_id") val xB3SpanId: String
    )

    data class ApiErrorResponse(
            val name: String,
            val status: Int,
            val timestamp: String,
            val code: Int,
            val resource: String,
            val detail: String,
            val metadata: Metadata
    )

}
