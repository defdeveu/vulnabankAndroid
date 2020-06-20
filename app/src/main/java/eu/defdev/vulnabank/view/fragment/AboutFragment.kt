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
import kotlinx.android.synthetic.main.fragment_about.*
import kotlinx.android.synthetic.main.fragment_settings.*
import javax.inject.Inject
import android.content.Intent
import android.net.Uri
import eu.defdev.vulnabank.BuildConfig


/**
 * About UI
 */
class AboutFragment : DaggerFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appVersion.text = getString(R.string.application_version, BuildConfig.VERSION_NAME)
        copyright.setOnClickListener { openUrl(getString(R.string.defdeveu_web)) }
        firstContributor.setOnClickListener { openUrl(getString(R.string.fgyozo_linkedin)) }
        secondContributor.setOnClickListener { openUrl(getString(R.string.zsombor_linkedin)) }
        thirdContributor.setOnClickListener { openUrl(getString(R.string.timur_linkedin)) }
    }

    private fun openUrl(url: String) =
        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))

    override fun onResume() {
        super.onResume()
        (activity as DaggerAppCompatActivity).supportActionBar?.title = getString(R.string.about)
    }

    companion object {
        const val TAG = "aboutFragment"
        private var INSTANCE: AboutFragment? = null

        fun getInstance(): AboutFragment {
            if (INSTANCE == null)
                INSTANCE =
                    AboutFragment()

            return INSTANCE!!
        }
    }
}