package com.shodaigram.backend.exception

class EtlException(
    message: String,
    cause: Throwable? = null,
) : RuntimeException(message, cause)
