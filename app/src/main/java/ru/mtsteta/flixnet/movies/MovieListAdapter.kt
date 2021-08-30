package ru.mtsteta.flixnet.movies

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils.loadAnimation
import android.widget.RatingBar
import android.widget.TextView
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.repo.MovieDto

class MovieListAdapter(private val clickListener: MovieClickListener) :
    PagingDataAdapter<MovieDto, MovieListAdapter.MovieListViewHolder>(MoviesDiffCallback()) {

    class MovieListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val posterImage: ShapeableImageView =
            itemView.findViewById(R.id.rvItemMoviePoster)

        private val titleTextView: TextView = itemView.findViewById(R.id.rvItemMovieTitle)

        private val infoTextView: TextView = itemView.findViewById(R.id.rvItemMovieInfo)

        private val ageLimitTextView: TextView =
            itemView.findViewById(R.id.rvItemAgeLimit)

        private val ratingBar: RatingBar = itemView.findViewById(R.id.rvItemMovieRating)

        fun bind(clickListener: MovieClickListener, item: MovieDto?, position: Int) {
            item?.run {
                titleTextView.text = item.title
                infoTextView.text = item.overview
                ratingBar.rating = item.rateScore?.let { it.toFloat() / 2.0f } ?: 0.0f
                ageLimitTextView.text = item.ageLimit
                posterImage.load(BuildConfig.BASE_IMAGE_URL + item.imageUrl) {
                    placeholder(R.drawable.loading_animation)
                    error(R.drawable.broken_image)
                }
                itemView.setOnClickListener { clickListener.onClick(item) }
            } ?: posterImage.load(R.drawable.loading_animation)
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MovieListViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.movie_rv_item, parent, false)
        return MovieListViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: MovieListViewHolder,
        position: Int
    ) {
        val repoItem = getItem(position)
        // Note that item may be null, ViewHolder must support binding null item as placeholder
        holder.bind(clickListener, repoItem, position)
        holder.itemView.animation = loadAnimation(holder.itemView.context, R.anim.rv_item_anim)

    }

    companion object {
        const val VIEW_TYPE_DATA = 0
        const val VIEW_TYPE_PROGRESS = 1
    }
}

private class MoviesDiffCallback : DiffUtil.ItemCallback<MovieDto>() {
    override fun areItemsTheSame(oldItem: MovieDto, newItem: MovieDto): Boolean {
        return oldItem.movie_id == newItem.movie_id
    }

    override fun areContentsTheSame(oldItem: MovieDto, newItem: MovieDto): Boolean {
        return oldItem == newItem
    }
}

class MovieClickListener(val clickListener: (movie: MovieDto) -> Unit) {
    fun onClick(movie: MovieDto) = clickListener(movie)
}
