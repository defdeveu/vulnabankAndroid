package eu.defdev.vulnabank.model.db

import android.database.Cursor
import androidx.lifecycle.LiveData
import androidx.room.*
import eu.defdev.vulnabank.model.Transaction

/**
 * Data access layer for transactions database
 */
@Dao
interface TransactionsDao {

    @Query("SELECT * from transactionData")
    fun findAllTransaction(): LiveData<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(item: Transaction): Long

    @Delete
    fun deleteTransaction(item: Transaction)

    /**
     * Provided data for content provider
     */
    @Query("SELECT * from transactionData")
    fun findAllTransactionExternal(): Cursor

    @Query("SELECT * from transactionData WHERE id = :id")
    fun findTransactionByIdExternal(id: Int): Cursor

    @Query("DELETE FROM ${Transaction.TABLE_NAME} WHERE id = :id")
    fun deleteTransactionByIdExternal(id: Int)
}