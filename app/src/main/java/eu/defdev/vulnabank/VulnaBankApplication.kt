package eu.defdev.vulnabank

import android.content.Intent
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import eu.defdev.vulnabank.di.DaggerApplicationComponent

/**
 * Custom application object for dependency injection initialization and handling application lifecycle.
 * When application comes to foreground from background, local broadcast message sent to proper activity to
 * handle pin request
 */
class VulnaBankApplication: DaggerApplication(), LifecycleObserver{
    override fun applicationInjector(): AndroidInjector<out DaggerApplication> {
        return DaggerApplicationComponent.builder()
            .application(this)
            .build()
    }

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(this)
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_START)
    fun onAppInForeground(){
        LocalBroadcastManager.getInstance(this).sendBroadcast(Intent(APP_IN_FOREGROUND))
    }

    companion object{
        const val APP_IN_FOREGROUND = "APP_IN_FOREGROUND"
    }
}