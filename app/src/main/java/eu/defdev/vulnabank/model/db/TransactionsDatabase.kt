package eu.defdev.vulnabank.model.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import eu.defdev.vulnabank.model.Transaction

/**
 * Room SQLite database initialization. Only one table defined for transactions in this database
 */
@Database(entities = [Transaction::class], version = 1, exportSchema = false)
abstract class TransactionsDatabase: RoomDatabase(){
    abstract fun transactionsDao(): TransactionsDao

    companion object {
        private var INSTANCE: TransactionsDatabase? = null

        fun getDataBase(context: Context): TransactionsDatabase? {
            if (INSTANCE == null){
                synchronized(TransactionsDatabase::class){
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        TransactionsDatabase::class.java, "transactionsDB").build()
                }
            }
            return INSTANCE
        }

        fun destroyDataBase(){
            INSTANCE = null
        }
    }
}