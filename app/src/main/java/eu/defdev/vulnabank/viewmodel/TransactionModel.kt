package eu.defdev.vulnabank.viewmodel

import androidx.lifecycle.*
import eu.defdev.vulnabank.model.Transaction
import eu.defdev.vulnabank.repository.transaction.TransactionRepository

/**
 * Transaction view model provides the data for transaction list UI
 */
class TransactionModel(transactionRepository: TransactionRepository) : ViewModel() {
    private val reloadTrigger = MutableLiveData<Boolean>()
    private val transactionListObservable: LiveData<List<Transaction>> = Transformations.switchMap(reloadTrigger) {
        transactionRepository.getTransactionList()
    }

    init {
        refreshTransactionList()
    }

    fun getTransactionObservable(): LiveData<List<Transaction>> = transactionListObservable

    fun refreshTransactionList() {
        reloadTrigger.value = true
    }
}