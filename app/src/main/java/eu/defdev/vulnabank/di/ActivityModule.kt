package eu.defdev.vulnabank.di

import dagger.Module
import dagger.android.ContributesAndroidInjector
import eu.defdev.vulnabank.view.LoginActivity
import eu.defdev.vulnabank.view.MainActivity

@Module
@Suppress("unused")
abstract class ActivityModule {
    @ContributesAndroidInjector
    internal abstract fun injectMainActivity(): MainActivity

    @ContributesAndroidInjector
    internal abstract fun injectLoginActivity(): LoginActivity
}