package ru.mtsteta.flixnet.genres

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.movies.MovieDto

class GenreListAdapter(private val items: List<String>, private val clickListener: GenreClickListener) :
    RecyclerView.Adapter<GenreListAdapter.GenreViewHolder>() {

    class GenreViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val genreTextView = itemView.findViewById<TextView>(R.id.tvGenreItem)

        fun bind(clickListener: GenreClickListener, item: String, position: Int) {
            genreTextView.text = item
            itemView.setOnClickListener { clickListener.onClick(item) }
        }
    }

    override fun getItemCount() = items.count()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GenreViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.genre_rv_item, parent, false)
        return GenreViewHolder(view)
    }

    override fun onBindViewHolder(holder: GenreViewHolder, position: Int) {
        holder.bind(clickListener, items[position], position)
    }
}

class GenreClickListener (val clickListener: (genre: String) -> Unit){
    fun onClick(genre: String) = clickListener(genre)
}