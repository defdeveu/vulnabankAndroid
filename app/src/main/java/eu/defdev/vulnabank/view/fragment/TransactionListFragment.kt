package eu.defdev.vulnabank.view.fragment

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.android.support.DaggerAppCompatActivity
import dagger.android.support.DaggerFragment
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.model.Transaction
import eu.defdev.vulnabank.model.error.ErrorMapper
import eu.defdev.vulnabank.repository.transaction.TransactionRepository
import eu.defdev.vulnabank.repository.util.CryptoUtil
import eu.defdev.vulnabank.view.MainActivity
import eu.defdev.vulnabank.view.adapter.TransactionListAdapter
import eu.defdev.vulnabank.viewmodel.TransactionModel
import kotlinx.android.synthetic.main.fragment_transaction_list.*
import javax.inject.Inject


class TransactionListFragment: DaggerFragment(){

    @Inject
    lateinit var viewModelFactory: ViewModelProvider.Factory

    @Inject
    lateinit var cryptoUtil: CryptoUtil

    @Inject
    lateinit var transactionRepository: TransactionRepository

    private lateinit var viewModel: TransactionModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_transaction_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = TransactionListAdapter { clickAction(it) }

        transactionSwipeRefreshLayout.setOnRefreshListener {
            viewModel.refreshTransactionList()
        }

        buttonStartTransaction.setOnClickListener {
            val dialogFragment = TransactionInitDialogFragment.getInstance()
            dialogFragment.setTargetFragment(this, LIST_TARGET)
            dialogFragment.show(activity!!.supportFragmentManager, "")
        }

        viewModel = ViewModelProviders.of(this, viewModelFactory).get(TransactionModel::class.java)

    }

    private fun clickAction(transaction: Transaction){
        val builder = AlertDialog.Builder(context)
        builder.setTitle(getString(R.string.delete_title))
        builder.setMessage(getString(R.string.delete_message, transaction.hash))

        builder.setPositiveButton(android.R.string.ok){dialog, _ ->
            transactionRepository.deleteTransactionById(transaction)
            dialog.cancel()
        }

        builder.setNegativeButton(android.R.string.cancel){dialog,_ ->
            dialog.cancel()
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == LIST_TARGET && resultCode == Activity.RESULT_OK){
            data?.getStringExtra(TransactionInitDialogFragment.INTENT_DATA)?.let {
                sendTransaction(it)
            }
        }
    }

    private fun sendTransaction(message: String) {
        Log.d("message TO server: ", "message: $message")
        progressBar.visibility = View.VISIBLE
        transactionRepository.sendTransactionMessage(message) {
            progressBar.visibility = View.GONE
            if (it.first != null) {
                Log.d("message FROM server: ", "message: ${it.first}")
                transactionRepository.insertTransactionItem(it.first!!)
            } else {
                val error = ErrorMapper.parseError(it.second)
                transactionRepository.insertTransactionItem(
                    Transaction(state = 1, hash = context?.getString(error), date = System.currentTimeMillis())
                )
                Toast.makeText(context, ErrorMapper.parseError(it.second), Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.settings -> {
                (activity as MainActivity).showSetting()
                true
            }
            R.id.about -> {
                (activity as MainActivity).showAbout()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        observeViewModel()
    }

    /**
     * Listen to view model transaction list changes
     */
    private fun observeViewModel() {
        transactionSwipeRefreshLayout.isRefreshing = true

        viewModel.getTransactionObservable().observe(this, Observer<List<Transaction>> {
            transactionSwipeRefreshLayout.isRefreshing = false
            (recyclerView.adapter as TransactionListAdapter).setItems(it)
            if (it.isEmpty()) emptyView.visibility = View.VISIBLE else emptyView.visibility = View.GONE
        })
    }

    override fun onResume() {
        super.onResume()
        (activity as DaggerAppCompatActivity).supportActionBar?.title = getString(R.string.transactions)
    }

    companion object{
        private var INSTANCE: TransactionListFragment? = null
        const val LIST_TARGET = 999

        fun getInstance(): TransactionListFragment {
            if (INSTANCE == null)
                INSTANCE =
                    TransactionListFragment()

            return INSTANCE!!
        }
    }
}