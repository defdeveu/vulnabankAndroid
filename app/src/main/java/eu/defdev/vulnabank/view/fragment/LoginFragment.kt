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
import kotlinx.android.synthetic.main.fragment_login.*
import javax.inject.Inject

class LoginFragment: DaggerFragment(){

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        password.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
                buttonLogin.isEnabled = p0?.length == 4
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                password.error = null
            }
        })

        buttonLogin.setOnClickListener {
            if (authRepository.login(password.text.toString()))
                (activity as LoginActivity).showMain()
            else
                password.error = getString(R.string.incorrect_password)
        }
    }

    companion object{
        private var INSTANCE: LoginFragment? = null

        fun getInstance(): LoginFragment {
            if (INSTANCE == null)
                INSTANCE =
                    LoginFragment()

            return INSTANCE!!
        }
    }
}