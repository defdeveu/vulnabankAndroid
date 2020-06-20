package eu.defdev.vulnabank.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerFragment
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.repository.auth.AuthRepository
import eu.defdev.vulnabank.view.LoginActivity
import kotlinx.android.synthetic.main.fragment_register.*
import javax.inject.Inject

class RegisterFragment: DaggerFragment(){

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_register, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        password.addTextChangedListener(textChangeListener { rePassword.error = null })
        rePassword.addTextChangedListener(textChangeListener { rePassword.error = null })

        buttonRegister.setOnClickListener {
            if (passwordsAreCorrect()) {
                authRepository.register(rePassword.text.toString())
                (activity as LoginActivity).showMain()
            } else
                rePassword.error = getString(R.string.incorrect_re_password)
        }
    }

    private fun textChangeListener(action: () -> Unit) = object: TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            manageButtonState()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            action.invoke()
        }
    }

    private fun manageButtonState(){
        buttonRegister.isEnabled = password.text.length == 4 && rePassword.text.length == 4
    }

    private fun passwordsAreCorrect(): Boolean {
        return password.text.isNotEmpty() && password.text.toString() == rePassword.text.toString()
    }

    companion object{
        private var INSTANCE: RegisterFragment? = null

        fun getInstance(): RegisterFragment {
            if (INSTANCE == null)
                INSTANCE =
                    RegisterFragment()

            return INSTANCE!!
        }
    }
}