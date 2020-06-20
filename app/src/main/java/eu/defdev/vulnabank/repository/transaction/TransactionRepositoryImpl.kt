package eu.defdev.vulnabank.repository.transaction

import android.content.Context
import android.util.Xml
import androidx.lifecycle.*
import eu.defdev.vulnabank.model.Request
import eu.defdev.vulnabank.model.Transaction
import eu.defdev.vulnabank.model.db.TransactionsDatabase
import eu.defdev.vulnabank.model.error.HttpError
import eu.defdev.vulnabank.repository.util.CryptoUtil
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayInputStream
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepositoryImpl @Inject constructor(val context: Context,
                                                    private val cryptoUtil: CryptoUtil,
                                                    private var transactionService: TransactionService):
    TransactionRepository {
    private var database = TransactionsDatabase.getDataBase(context)

    override fun getTransactionList(): LiveData<List<Transaction>> {
        return database!!.transactionsDao().findAllTransaction()
    }

    override fun insertTransactionItem(item: Transaction) {
        GlobalScope.launch {
            database?.transactionsDao()?.insertTransaction(item)
        }
    }

    override fun sendTransactionMessage(message: String, callback: (Pair<Transaction?, Throwable?>) -> Unit) {
        GlobalScope.launch {
            val encMessage = cryptoUtil.encryptAES(message)
            val transactionDto = Request(
                cryptoUtil.encryptRSA(),
                encMessage,
                cryptoUtil.signRSA(encMessage)
            )

            transactionService.sendTransaction(transactionDto).enqueue(object : Callback<String>{
                override fun onFailure(call: Call<String>, throwable: Throwable) {
                    callback.invoke(Pair(null, throwable))
                }

                override fun onResponse(call: Call<String>, response: Response<String>) {
                    if (response.isSuccessful)
                        callback.invoke(Pair(decryptMessate(response.body().toString()), null))
                    else
                        callback.invoke(Pair(null, HttpError(response.code())))
                }
            })
        }
    }

    private fun decryptMessate(message: String): Transaction{
        return parse(ByteArrayInputStream(cryptoUtil.decryptAES(message).toByteArray(Charsets.UTF_8)))
    }

    override fun deleteTransactionById(transaction: Transaction) {
        GlobalScope.launch {
            database?.transactionsDao()?.deleteTransaction(transaction)
        }
    }

    /**
     * Helper function for local decrypted string xml parsing
     *
     * @param inputStream the XML content
     * @return Transaction object
     */
    @Throws(XmlPullParserException::class, IOException::class)
    fun parse(inputStream: InputStream): Transaction {
        inputStream.use { inputStream ->
            val parser: XmlPullParser = Xml.newPullParser()
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
            parser.setInput(inputStream, null)
            parser.nextTag()

            return readText(parser)
        }
    }

    /**
     * Read the text contents from xml and convert it to Transaction object
     *
     * @param parser the XML parser
     * @return transaction object
     */
    @Throws(IOException::class, XmlPullParserException::class)
    private fun readText(parser: XmlPullParser): Transaction {
        var code = ""
        var hash = ""
        if (parser.next() == XmlPullParser.TEXT) {
            code = parser.text
            parser.next()
            parser.nextTag()
            parser.next()
            hash = parser.text
        }
        return Transaction(state = code.toInt(), hash = hash, date = System.currentTimeMillis())
    }
}