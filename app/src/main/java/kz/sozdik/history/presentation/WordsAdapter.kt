package kz.sozdik.history.presentation

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import kz.sozdik.R

import kz.sozdik.dictionary.domain.model.Word

class WordsAdapter(
    private val items: List<Word>,
    private val onItemClick: (word: Word) -> Unit,
    private val onItemLongClick: (word: Word) -> Unit
) : RecyclerView.Adapter<WordsAdapter.WordViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return WordViewHolder(inflater.inflate(R.layout.item_word, parent, false))
    }

    override fun onBindViewHolder(holder: WordViewHolder, position: Int) {
        val word = items[position]
        holder.tvWord.text = word.phrase
        holder.itemView.setOnClickListener {
            onItemClick.invoke(word)
        }
        holder.itemView.setOnLongClickListener {
            onItemLongClick.invoke(word)
            true
        }
    }

    override fun getItemCount(): Int = items.size

    inner class WordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvWord: TextView = itemView.findViewById(R.id.tvWord)
    }
}