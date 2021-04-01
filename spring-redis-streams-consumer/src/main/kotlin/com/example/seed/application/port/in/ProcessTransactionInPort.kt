package com.example.seed.application.port.`in`

import com.example.seed.domain.Transaction

interface ProcessTransactionInPort {

    fun execute(trx: Transaction): Transaction

}
