package ru.mtsteta.flixnet.movies


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.repo.MovieDto

class MovieListAdapter(private val clickListener: MovieClickListener) :
    ListAdapter<MovieDto, MovieListAdapter.MovieListViewHolder>(DiffCallback) {

    class MovieListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val posterImage: ImageView =
            itemView.findViewById(R.id.rvItemMoviePoster)

        private val titleTextView: TextView = itemView.findViewById(R.id.rvItemMovieTitle)

        private val infoTextView: TextView = itemView.findViewById(R.id.rvItemMovieInfo)

        private val ageLimitTextView: TextView =
            itemView.findViewById(R.id.rvItemAgeLimit)

        private val ratingBar: RatingBar = itemView.findViewById(R.id.rvItemMovieRating)

        fun bind(clickListener: MovieClickListener, item: MovieDto, position: Int) {
            titleTextView.text = item.title
            infoTextView.text = item.description
            ratingBar.rating = item.rateScore.toFloat()
            ageLimitTextView.text = "${item.ageLimit}+"
            posterImage.load(item.imageUrl) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.broken_image)
            }
            itemView.setOnClickListener { clickListener.onClick(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieListViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.movies_rv_item, parent, false)
        return MovieListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MovieListViewHolder,
        position: Int
    ) {
        holder.bind(clickListener, getItem(position), position)
    }

    companion object DiffCallback : DiffUtil.ItemCallback<MovieDto>() {
        override fun areItemsTheSame(oldItem: MovieDto, newItem: MovieDto): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: MovieDto, newItem: MovieDto): Boolean {
            return oldItem.title == newItem.title
        }
    }
}

class MovieClickListener(val clickListener: (movie: MovieDto) -> Unit) {
    fun onClick(movie: MovieDto) = clickListener(movie)
}
