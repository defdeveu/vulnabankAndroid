package eu.defdev.vulnabank.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import androidx.lifecycle.ViewModelProvider
import dagger.Module
import dagger.Provides
import eu.defdev.vulnabank.repository.auth.AuthRepository
import eu.defdev.vulnabank.repository.auth.AuthRepositoryImpl
import eu.defdev.vulnabank.repository.transaction.TransactionRepository
import eu.defdev.vulnabank.repository.transaction.TransactionRepositoryImpl
import eu.defdev.vulnabank.repository.transaction.TransactionService
import eu.defdev.vulnabank.repository.util.CryptoUtil
import eu.defdev.vulnabank.repository.util.CryptoUtilImpl
import eu.defdev.vulnabank.viewmodel.Factory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import javax.inject.Singleton

@Module
@Suppress("unused")
class ApplicationModule {

    @Provides
    @Singleton
    internal fun provideApplicationContext(application: Application): Context = application.baseContext

    @Provides
    internal fun provideTransactionRepository(transactionRepositoryImpl: TransactionRepositoryImpl): TransactionRepository = transactionRepositoryImpl

    @Provides
    internal fun provideTransactionViewModelFactory(transactionRepository: TransactionRepository): ViewModelProvider.Factory = Factory(transactionRepository)

    @Provides
    @Singleton
    fun provideSharedPreference(application: Context) : SharedPreferences = application.getSharedPreferences("shared_pref", Context.MODE_PRIVATE)

    @Provides
    @Singleton
    fun provideAuthRepository(authUtilImpl: AuthRepositoryImpl) : AuthRepository = authUtilImpl

    @Provides
    @Singleton
    fun provideCryptoUtil(cryptoUtil: CryptoUtilImpl) : CryptoUtil = cryptoUtil

    @Provides
    @Singleton
    internal fun provideTransactionService(authRepository: AuthRepository): TransactionService {
        val logging = HttpLoggingInterceptor()
        logging.apply { logging.level = HttpLoggingInterceptor.Level.BODY }

        val client = OkHttpClient.Builder()
            .addInterceptor(logging)
            .build()

        return Retrofit.Builder().baseUrl(authRepository.getServerAddress())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .client(client)
            .build().create(TransactionService::class.java)
    }
}