package com.shodaigram.backend.exception

/**
 * Thrown when filter parameters are invalid.
 */
class InvalidFilterException(message: String) : RuntimeException(message)

/**
 * Thrown when a requested tag is not found.
 */
class TagNotFoundException(message: String) : RuntimeException(message)
