package eu.defdev.vulnabank.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import eu.defdev.vulnabank.view.fragment.*

@Module
@Suppress("unused")
abstract class FragmentModule {
    @ContributesAndroidInjector
    internal abstract fun injectTransactionListFragment(): TransactionListFragment

    @ContributesAndroidInjector
    internal abstract fun injectLoginFragment(): LoginFragment

    @ContributesAndroidInjector
    internal abstract fun injectRegisterFragment(): RegisterFragment

    @ContributesAndroidInjector
    internal abstract fun injectSettingsFragment(): SettingsFragment

    @ContributesAndroidInjector
    internal abstract fun injectAboutFragment(): AboutFragment

    @ContributesAndroidInjector
    internal abstract fun injectTransactionInitDialogFragment(): TransactionInitDialogFragment
}