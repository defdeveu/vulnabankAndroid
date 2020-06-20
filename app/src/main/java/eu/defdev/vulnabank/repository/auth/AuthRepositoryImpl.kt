package eu.defdev.vulnabank.repository.auth

import android.content.Context
import android.content.SharedPreferences
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.repository.util.CryptoUtil
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(val context: Context,
                                             private var sharedPreferences: SharedPreferences,
                                             private var cryptoUtil: CryptoUtil): AuthRepository {
    override fun getServerAddress(): String {
        var serverAddress = sharedPreferences.getString(SERVER_ADDRESS, null)
        if (serverAddress.isNullOrEmpty()) {
            serverAddress =  context.getString(R.string.server_url)
            setServerAddress(serverAddress)
        }

        return serverAddress
    }

    override fun setServerAddress(serverAddress: String) {
        sharedPreferences.edit().putString(SERVER_ADDRESS, serverAddress).apply()
    }


    override fun register(password: String) =
        sharedPreferences.edit().putString(PASSWORD, password.md5()).apply()

    override fun login(password: String): Boolean =
        sharedPreferences.getString(PASSWORD, null) ?: "".md5() == password.md5()

    override fun hasRegistration(): Boolean =
        sharedPreferences.contains(PASSWORD)


    companion object {
        const val PASSWORD = "PASSWORD"
        const val SERVER_ADDRESS = "SERVER_ADDRESS"
    }

    private fun String.md5(): String = cryptoUtil.convertPinToMD5(this)
}