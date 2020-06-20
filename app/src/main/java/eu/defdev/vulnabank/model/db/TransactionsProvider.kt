package eu.defdev.vulnabank.model.db

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri

import eu.defdev.vulnabank.model.Transaction

/**
 * Content provider to share transaction database content with another applications.
 * Available operations: query, insert, delete
 */
class TransactionsProvider : ContentProvider() {

    override fun onCreate(): Boolean {
        return true
    }

    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        val code = MATCHER.match(uri)
        if (code == CODE_TRANSACTIONS_DIR || code == CODE_TRANSACTIONS_ITEM) {
            val context = context ?: return null
            val transactionsDao = TransactionsDatabase.getDataBase(context)!!.transactionsDao()
            val cursor: Cursor
            cursor = if (code == CODE_TRANSACTIONS_ITEM) {
                transactionsDao.findAllTransactionExternal()
            } else {
                transactionsDao.findTransactionByIdExternal(ContentUris.parseId(uri).toInt())
            }
            cursor.setNotificationUri(context.contentResolver, uri)
            return cursor
        } else {
            throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun getType(uri: Uri): String? = when (MATCHER.match(uri)) {
            CODE_TRANSACTIONS_DIR -> "vnd.android.cursor.dir/" + AUTHORITY + "." + Transaction.TABLE_NAME
            CODE_TRANSACTIONS_ITEM -> "vnd.android.cursor.item/" + AUTHORITY + "." + Transaction.TABLE_NAME
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        when (MATCHER.match(uri)) {
            CODE_TRANSACTIONS_DIR -> {
                val context = context ?: return null
                val id = TransactionsDatabase.getDataBase(context)!!.transactionsDao()
                    .insertTransaction(Transaction.fromContentValues(contentValues!!))
                context.contentResolver.notifyChange(uri, null)
                return ContentUris.withAppendedId(uri, id)
            }
            CODE_TRANSACTIONS_ITEM -> throw IllegalArgumentException("Invalid URI, cannot insert with ID: $uri")
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun delete(uri: Uri, s: String?, strings: Array<String>?): Int {
        when (MATCHER.match(uri)) {
            CODE_TRANSACTIONS_DIR -> throw IllegalArgumentException("Invalid URI, cannot update without ID$uri")
            CODE_TRANSACTIONS_ITEM -> {
                val context = context ?: return 0
                TransactionsDatabase.getDataBase(context)!!.transactionsDao()
                    .deleteTransactionByIdExternal(ContentUris.parseId(uri).toInt())
                context.contentResolver.notifyChange(uri, null)
                return 1
            }
            else -> throw IllegalArgumentException("Unknown URI: $uri")
        }
    }

    override fun update(
        uri: Uri,
        contentValues: ContentValues?,
        s: String?,
        strings: Array<String>?
    ): Int {
        return 0
    }

    companion object {
        const val AUTHORITY = "eu.defdev.vulnabank.transactions"

        private const val CODE_TRANSACTIONS_DIR = 1

        private const val CODE_TRANSACTIONS_ITEM = 2

        private val MATCHER = UriMatcher(UriMatcher.NO_MATCH)

        init {
            MATCHER.addURI(AUTHORITY, Transaction.TABLE_NAME, CODE_TRANSACTIONS_DIR)
            MATCHER.addURI(AUTHORITY, Transaction.TABLE_NAME + "/*", CODE_TRANSACTIONS_ITEM)
        }
    }
}
