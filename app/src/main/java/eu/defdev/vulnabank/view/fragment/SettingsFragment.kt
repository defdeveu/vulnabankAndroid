package eu.defdev.vulnabank.view.fragment

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.repository.auth.AuthRepository
import eu.defdev.vulnabank.view.MainActivity
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject

/**
 * Settings UI to setup ssl connection usage and pin change
 */
class SettingsFragment : DaggerFragment() {

    @Inject
    lateinit var authRepository: AuthRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupPasswordChange()
        setupSSLCheckbox()
    }

    private fun setupSSLCheckbox() {
        sslEnabled.isChecked = authRepository.getServerAddress() ==
                context!!.getString(R.string.server_url_ssl)

        sslEnabled.setOnCheckedChangeListener { _, checked ->
            Toast.makeText(
                context,
                getString(R.string.tls_change_restart_notification),
                Toast.LENGTH_LONG
            ).show()
            if (checked)
                authRepository.setServerAddress(context!!.getString(R.string.server_url_ssl))
            else
                authRepository.setServerAddress(context!!.getString(R.string.server_url))
        }
    }

    private fun setupPasswordChange() {
        newPassword.addTextChangedListener(textChangeListener { reNewPassword.error = null })
        reNewPassword.addTextChangedListener(textChangeListener { reNewPassword.error = null })

        buttonChangePassword.setOnClickListener {
            if (passwordsAreCorrect()) {
                authRepository.register(reNewPassword.text.toString())
                Toast.makeText(context, getString(R.string.password_change_done), Toast.LENGTH_LONG)
                    .show()
                reNewPassword.text.clear()
                newPassword.text.clear()
                (activity as MainActivity).showMain()
            } else
                reNewPassword.error = getString(R.string.incorrect_re_password)
        }
    }

    override fun onResume() {
        super.onResume()
        (activity as DaggerAppCompatActivity).supportActionBar?.title = getString(R.string.settings)
    }

    private fun textChangeListener(action: () -> Unit) = object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {
            manageButtonState()
        }

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            action.invoke()
        }
    }

    private fun manageButtonState() {
        buttonChangePassword.isEnabled = reNewPassword.text.length == 4 && newPassword.text.length == 4
    }

    private fun passwordsAreCorrect(): Boolean {
        return newPassword.text.toString() == reNewPassword.text.toString()
    }

    companion object {
        const val TAG = "registerFragment"
        private var INSTANCE: SettingsFragment? = null

        fun getInstance(): SettingsFragment {
            if (INSTANCE == null)
                INSTANCE =
                    SettingsFragment()

            return INSTANCE!!
        }
    }
}