package eu.defdev.vulnabank.model

/**
 * Dto object for retrofit server request. This object contains the encrypted message, key and signature
 */
data class Request(val enckey: String, val message: String, val signature: String)