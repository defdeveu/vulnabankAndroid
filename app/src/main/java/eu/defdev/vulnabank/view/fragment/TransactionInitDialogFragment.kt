package eu.defdev.vulnabank.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.android.support.DaggerAppCompatDialogFragment
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.repository.transaction.TransactionRepository
import kotlinx.android.synthetic.main.fragment_transaction_init.accountNumber
import kotlinx.android.synthetic.main.fragment_transaction_init.amount
import kotlinx.android.synthetic.main.fragment_transaction_init.cancel
import kotlinx.android.synthetic.main.fragment_transaction_init.comment
import kotlinx.android.synthetic.main.fragment_transaction_init.name
import kotlinx.android.synthetic.main.fragment_transaction_init.ok
import javax.inject.Inject

class TransactionInitDialogFragment: DaggerAppCompatDialogFragment() {
    @Inject
    lateinit var transactionRepository: TransactionRepository

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_init, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ok.setOnClickListener {
            val dataIntent = Intent()
            val msg = getStringContentOfTransaction()
            clearTexts()
            dataIntent.putExtra(INTENT_DATA, msg)
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_OK, dataIntent)
            dismiss()
        }

        cancel.setOnClickListener {
            clearTexts()
            targetFragment?.onActivityResult(targetRequestCode, Activity.RESULT_CANCELED, null)
            dismiss()
        }

        amount.addTextChangedListener(textChangeListener {amount.error = null})
        name.addTextChangedListener(textChangeListener {amount.error = null})
        accountNumber.addTextChangedListener(textChangeListener {amount.error = null})
    }

    private fun clearTexts(){
        amount.text.clear()
        name.text.clear()
        accountNumber.text.clear()
        comment.text.clear()
    }

    private fun getStringContentOfTransaction(): String {
        return "${name.text};" +
                "${accountNumber.text};" +
                "${amount.text};" +
                "${comment.text}";
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
        if(name.text.isEmpty()) {
            ok.isEnabled = false
            name.error = context?.getString(R.string.name_error)
            return
        }

        if (accountNumber.text.length < 8) {
            ok.isEnabled = false
            accountNumber.error = context?.getString(R.string.account_number_error)
            return
        }

        if (amount.text.isEmpty() || amount.text.toString().toInt() <= 0){
            ok.isEnabled = false
            amount.error = context?.getString(R.string.amount_error)
            return
        }

        ok.isEnabled = true
    }

    companion object{
        const val INTENT_DATA = "intentData"
        private var INSTANCE: TransactionInitDialogFragment? = null

        fun getInstance(): TransactionInitDialogFragment {
            if (INSTANCE == null)
                INSTANCE =
                    TransactionInitDialogFragment()

            return INSTANCE!!
        }
    }
}