package com.example.seed.adapter.controller

import com.example.seed.adapter.controller.model.CreateTransactionControllerModel
import com.example.seed.adapter.controller.model.TransactionControllerModel
import com.example.seed.application.port.`in`.CreateTransactionInPort
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RequestBody
import java.util.concurrent.CompletableFuture

@RestController
@RequestMapping("/api/v1/transactions")
class TransactionControllerAdapter(
        private val createTransactionInPort: CreateTransactionInPort
) {

    private val log = LoggerFactory.getLogger(this.javaClass)

    @PostMapping(consumes = [MediaType.APPLICATION_JSON_VALUE], produces = [MediaType.APPLICATION_JSON_VALUE])
    @ResponseStatus(HttpStatus.ACCEPTED)
    fun create(
            @RequestBody @Validated request: CreateTransactionControllerModel
    ): CompletableFuture<TransactionControllerModel> =
            request.also { log.info("Received request {}", it) }
                    .let { request.toDomain() }
                    .let { cmd ->
                        createTransactionInPort.execute(cmd)
                                .thenApply { TransactionControllerModel.fromDomain(it) }
                    }

}
