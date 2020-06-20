package eu.defdev.vulnabank.view

import android.content.Intent
import android.os.Bundle
import dagger.android.support.DaggerAppCompatActivity
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.repository.auth.AuthRepository
import eu.defdev.vulnabank.view.fragment.LoginFragment
import eu.defdev.vulnabank.view.fragment.RegisterFragment
import javax.inject.Inject


class LoginActivity : DaggerAppCompatActivity() {
    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        if (authRepository.hasRegistration())
            showLogin()
        else
            showRegister()
    }

    internal fun showMain(){
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showLogin(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, LoginFragment.getInstance())
            .disallowAddToBackStack()
            .commit()
    }

    private fun showRegister(){
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.container, RegisterFragment.getInstance())
            .disallowAddToBackStack()
            .commit()
    }
}
