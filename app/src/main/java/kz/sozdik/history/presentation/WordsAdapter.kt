package kz.sozdik.history.presentation

import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import kz.sozdik.databinding.ItemWordBinding
import kz.sozdik.dictionary.domain.model.Word

class WordsAdapter(
    private val items: List<Word>,
    private val onItemClick: (word: Word) -> Unit,
    private val onItemLongClick: (word: Word) -> Unit
) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemWordBinding.inflate(inflater, parent, false)
        return WordViewHolder(binding.root)
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = items[position]
        holder.binding?.word = word
        holder.binding?.executePendingBindings()
        holder.binding?.root?.setOnClickListener {
            onItemClick.invoke(word)
        }
        holder.binding?.root?.setOnLongClickListener {
            onItemLongClick.invoke(word)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        internal var binding: ItemWordBinding? = null

        init {
            binding = DataBindingUtil.bind(itemView)
        }
    }
}