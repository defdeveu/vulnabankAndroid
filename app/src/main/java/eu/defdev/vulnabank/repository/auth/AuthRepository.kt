package eu.defdev.vulnabank.repository.auth

/**
 * Repository which use shared preferences to store user pin in mp5 hash format and server address.
 */
interface AuthRepository {
    fun register(password: String)
    fun login(password: String): Boolean
    fun hasRegistration(): Boolean
    fun getServerAddress():String
    fun setServerAddress(serverAddress: String)
}