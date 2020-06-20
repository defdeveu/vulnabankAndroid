package eu.defdev.vulnabank.repository.transaction

import eu.defdev.vulnabank.model.Request
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

/**
 * Transaction service for retrofit communication module. The transaction communication interface defined in here
 */
interface TransactionService {
    @POST("/request")
    @Headers("Content-Type: text/xml", "Accept-Charset: utf-8")
    fun sendTransaction(@Body message: Request): Call<String>
}