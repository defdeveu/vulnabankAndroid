package eu.defdev.vulnabank.repository.util

import android.content.Context
import android.util.Base64
import androidx.annotation.RawRes
import eu.defdev.vulnabank.R
import java.io.InputStream
import java.math.BigInteger
import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec
import javax.inject.Inject

class CryptoUtilImpl @Inject constructor(var context: Context): CryptoUtil{
    override fun convertPinToMD5(plainPin: String): String {
        val intValue = BigInteger(1, MessageDigest.getInstance("MD5").digest(plainPin.toByteArray()))
        return intValue.toString(16).padStart(32, '0')
    }

    override fun encryptAES(message: String): String {
        val key = SecretKeySpec(AES_KEY.toByteArray(), CRYPTO_METHOD_AES)
        val ivSpec = IvParameterSpec(AES_IV.toByteArray())
        val cipher = Cipher.getInstance(CRYPTO_TRANSFORM_AES)

        cipher.init(Cipher.ENCRYPT_MODE, key, ivSpec)

        return Base64.encodeToString(cipher.doFinal(message.toByteArray()), Base64.DEFAULT)
    }

    override fun decryptAES(message: String): String {
        val key = SecretKeySpec(AES_KEY.toByteArray(), CRYPTO_METHOD_AES)
        val ivSpec = IvParameterSpec(AES_IV.toByteArray())
        val cipher = Cipher.getInstance(CRYPTO_TRANSFORM_AES)

        cipher.init(Cipher.DECRYPT_MODE, key, ivSpec)

        return String(cipher.doFinal(Base64.decode(message, Base64.DEFAULT)))
    }

    override fun encryptRSA(): String {
        val cipher = Cipher.getInstance(CRYPTO_TRANSFORM_RSA)

        cipher.init(Cipher.ENCRYPT_MODE, getKeyFromResource(R.raw.server_public_pkcs8).toPublicKey())

        return Base64.encodeToString(cipher.doFinal(("$AES_KEY|$AES_IV").toByteArray()), Base64.DEFAULT)
    }

    override fun signRSA(message: String): String {
        val signature = Signature.getInstance("SHA1withRSA")
        signature.initSign(getKeyFromResource(R.raw.client_private_pkcs8_2).toPrivateKey())
        signature.update(Base64.decode(message, Base64.DEFAULT))

        return Base64.encodeToString(signature.sign(), Base64.DEFAULT)
    }

    private fun getKeyFromResource(@RawRes rawRes: Int): InputStream =
        context.resources.openRawResource(rawRes)


    private fun InputStream.toPublicKey(): PublicKey {
        val encodedKey = ByteArray(this.available())
        this.read(encodedKey)
        return KeyFactory.getInstance(CRYPTO_METHOD_RSA).generatePublic(X509EncodedKeySpec(encodedKey))
    }

    private fun InputStream.toPrivateKey(): PrivateKey {
        val encodedKey = ByteArray(this.available())
        this.read(encodedKey)
        return KeyFactory.getInstance(CRYPTO_METHOD_RSA).generatePrivate(PKCS8EncodedKeySpec(encodedKey))
    }

    /**
     * transform, methods and AES keys defined in this private static context
     */
    companion object {
        private const val CRYPTO_METHOD_RSA = "RSA"
        private const val CRYPTO_TRANSFORM_RSA = "RSA/ECB/PKCS1PADDING"
        private const val CRYPTO_METHOD_AES = "AES"
        private const val CRYPTO_TRANSFORM_AES = "AES/CBC/PKCS7Padding"

        private const val AES_KEY = "00112233445566778899aabbccddeeff"
        private const val AES_IV = "1111111111111111"
    }
}