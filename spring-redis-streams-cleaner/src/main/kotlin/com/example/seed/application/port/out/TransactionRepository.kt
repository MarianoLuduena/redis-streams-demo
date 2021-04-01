package com.example.seed.application.port.out

interface TransactionRepository {

    fun trim(maxLength: Int)

}
