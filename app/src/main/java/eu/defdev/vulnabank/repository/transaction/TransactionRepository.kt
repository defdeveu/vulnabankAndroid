package eu.defdev.vulnabank.repository.transaction

import androidx.lifecycle.LiveData
import eu.defdev.vulnabank.model.Transaction

/**
 * Repository for transaction management. This interface provides access to local sqlite database transactions
 * table and handles the server communication.
 */
interface TransactionRepository {
    fun getTransactionList(): LiveData<List<Transaction>>
    fun insertTransactionItem(item: Transaction)
    fun deleteTransactionById(transaction: Transaction)
    fun sendTransactionMessage(message: String, callback: (Pair<Transaction?, Throwable?>) -> Unit)
}