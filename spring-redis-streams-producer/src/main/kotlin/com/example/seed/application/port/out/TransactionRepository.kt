package com.example.seed.application.port.out

import com.example.seed.application.port.`in`.CreateTransactionInPort
import com.example.seed.domain.Transaction

interface TransactionRepository {

    fun save(cmd: CreateTransactionInPort.CreateTransactionCmd): Transaction

}
