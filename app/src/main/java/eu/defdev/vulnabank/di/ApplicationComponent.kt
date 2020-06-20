package eu.defdev.vulnabank.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import dagger.android.support.AndroidSupportInjectionModule
import javax.inject.Singleton

@Singleton
@Component(modules = [AndroidSupportInjectionModule::class, ApplicationModule::class,
    ActivityModule::class, FragmentModule::class])
interface ApplicationComponent : AndroidInjector<DaggerApplication> {
    override fun inject(instance: DaggerApplication)

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(application: Application): Builder

        fun build(): ApplicationComponent
    }
}