package eu.defdev.vulnabank.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.content.ContentValues

/**
 * Helper class to store transaction data
 */
@Entity(tableName = Transaction.TABLE_NAME)
data class Transaction(@PrimaryKey(autoGenerate = true) var id: Int ?= null,
                       var state: Int, var hash: String? = null, var date: Long) {

    companion object {
        const val TABLE_NAME = "transactionData"

        /**
         * Transform function for content provider. Read proper data from content values
         *
         * @param values the content values
         * @return generated transaction
         */
        fun fromContentValues(values: ContentValues): Transaction {
            var state = 0
            if (values.containsKey("state")) {
                state = values.getAsInteger("state")
            }

            var hash: String? = null
            if (values.containsKey("hash")) {
                hash = values.getAsString("hash")
            }

            var date = System.currentTimeMillis()
            if (values.containsKey("date")) {
                date = values.getAsLong("date")
            }

            return Transaction(state = state, hash = hash, date = date)
        }
    }
}