package ru.mtsteta.flixnet.genres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mtsteta.flixnet.R

class GenreListAdapter(private val items: List<String>) :
    RecyclerView.Adapter<GenreListAdapter.GenreViewHolder>() {

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val genreTextView = itemView.findViewById<TextView>(R.id.tvGenreItem)

        fun bind(item: String, position: Int) {
            genreTextView.text = item
        }
    }

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.genre_rv_item, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(items[position], position)
    }
}