package eu.defdev.vulnabank.view

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.FragmentManager
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import dagger.android.support.DaggerAppCompatActivity
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.VulnaBankApplication
import eu.defdev.vulnabank.model.db.TransactionsDatabase
import eu.defdev.vulnabank.view.fragment.AboutFragment
import eu.defdev.vulnabank.view.fragment.SettingsFragment
import eu.defdev.vulnabank.view.fragment.TransactionListFragment


class MainActivity : DaggerAppCompatActivity() {

    private val foregroundBroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            finish()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        showMain()
        setSupportActionBar(findViewById(R.id.toolbar))

        LocalBroadcastManager.getInstance(this)
            .registerReceiver(foregroundBroadcastReceiver,
                IntentFilter(VulnaBankApplication.APP_IN_FOREGROUND))
    }

    internal fun showMain(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, TransactionListFragment.getInstance())
            .commit()
    }

    internal fun showSetting(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, SettingsFragment.getInstance())
            .addToBackStack(SettingsFragment.TAG)
            .commit()
    }

    internal fun showAbout(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, AboutFragment.getInstance())
            .addToBackStack(AboutFragment.TAG)
            .commit()
    }

    override fun onDestroy() {
        super.onDestroy()
        TransactionsDatabase.destroyDataBase()
        LocalBroadcastManager.getInstance(this).unregisterReceiver(foregroundBroadcastReceiver)
    }
}
