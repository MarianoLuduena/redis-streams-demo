package com.example.seed.config

enum class SPError(val errorCode: Int, val defaultMessage: String) {

    INTERNAL_ERROR(100, "Internal server error"),
    RESOURCE_NOT_FOUND(101, "Requested resource not found"),
    INVALID_COMMAND(102, "Invalid command")

}
