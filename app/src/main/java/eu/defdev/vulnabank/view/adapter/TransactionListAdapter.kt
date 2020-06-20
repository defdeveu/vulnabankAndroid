package eu.defdev.vulnabank.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import eu.defdev.vulnabank.R
import eu.defdev.vulnabank.model.Transaction
import kotlinx.android.synthetic.main.card_view_transaction.view.*
import java.text.SimpleDateFormat
import java.util.*

class TransactionListAdapter(private val listener: (Transaction) -> Unit): RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var items: List<Transaction> = emptyList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder = ViewHolderTransaction(
        LayoutInflater.from(parent.context).inflate(R.layout.card_view_transaction, parent, false)
    )

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int){
        (holder as ViewHolderTransaction).bind(items[position], listener)
    }

    override fun getItemCount() = items.size

    fun setItems(itemList: List<Transaction>){
        items = itemList.sortedByDescending { it.date }
        notifyDataSetChanged()
    }

    @SuppressLint("PrivateResource")
    class ViewHolderTransaction(itemView: View) : RecyclerView.ViewHolder(itemView){
        fun bind(item: Transaction, listener: (Transaction) -> Unit) = with(itemView) {
            when (item.state){
                0 -> {
                    textViewState.text = context.getString(R.string.success)
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.cardview_light_background))
                }
                1 -> {
                    textViewState.text = context.getString(R.string.fail)
                    cardView.setCardBackgroundColor(ContextCompat.getColor(context, R.color.colorError))
                }
            }
            buttonDelete.setOnClickListener { listener.invoke(item) }
            textViewHash.text = item.hash
            textViewDate.text = SimpleDateFormat.getInstance().format(Date(item.date))
        }
    }
}