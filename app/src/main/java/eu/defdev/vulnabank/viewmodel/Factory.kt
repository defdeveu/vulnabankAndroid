package eu.defdev.vulnabank.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import eu.defdev.vulnabank.repository.transaction.TransactionRepository

/**
 * helper factory to view model creation
 */
@Suppress("UNCHECKED_CAST")
class Factory(private val transactionRepository: TransactionRepository) :
    ViewModelProvider.NewInstanceFactory() {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            TransactionModel::class.java -> TransactionModel(transactionRepository) as T
            else ->  throw IllegalArgumentException("ViewModel Not Found")
        }
    }
}