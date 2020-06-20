package eu.defdev.vulnabank.repository.util

/**
 * Util class for crypto - RSA and AES encryption and decryption
 */
interface CryptoUtil {
    fun encryptAES(message: String): String
    fun decryptAES(message: String): String
    fun encryptRSA(): String
    fun signRSA(message: String): String
    fun convertPinToMD5(plainPin: String): String
}