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
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.repo.MovieDto

class MovieListAdapter(private val clickListener: MovieClickListener) :
    ListAdapter<MovieDto, RecyclerView.ViewHolder>(MoviesDiffCallback()) {

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
            infoTextView.text = item.overview
            ratingBar.rating = item.rateScore.toFloat() / 2.0f
            ageLimitTextView.text = item.ageLimit
            posterImage.load(BuildConfig.BASE_IMAGE_URL + item.imageUrl) {
                placeholder(R.drawable.loading_animation)
                error(R.drawable.broken_image)
            }
            itemView.setOnClickListener { clickListener.onClick(item) }
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEW_TYPE_DATA -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.movies_rv_item, parent, false)
                MovieListViewHolder(view)
            }
            VIEW_TYPE_PROGRESS -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.progress_bar, parent, false)
                ProgressViewHolder(view)
            }
            else -> throw IllegalArgumentException("Different View type")
        }
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int
    ) {
        (holder as? MovieListViewHolder)?.bind(clickListener, getItem(position), position)
    }

    override fun onCurrentListChanged(
        previousList: MutableList<MovieDto>,
        currentList: MutableList<MovieDto>
    ) {
        super.onCurrentListChanged(previousList, currentList)
    }

    inner class ProgressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    companion object {
        const val VIEW_TYPE_DATA = 0;
        const val VIEW_TYPE_PROGRESS = 1;
    }
}

class RedditAdapter :
    PagingDataAdapter<RedditPost, RedditAdapter.RedditViewHolder>(DiffUtilCallBack()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RedditViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.adapter_row, parent, false)
        return RedditViewHolder(view)
    }

    override fun onBindViewHolder(holder: RedditViewHolder, position: Int) {
        getItem(position)?.let { holder.bindPost(it) }
    }

    class RedditViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val scoreText: TextView = itemView.score
        private val commentsText: TextView = itemView.comments
        private val titleText: TextView = itemView.title

        fun bindPost(redditPost: RedditPost) {
            with(redditPost) {
                scoreText.text = score.toString()
                commentsText.text = commentCount.toString()
                titleText.text = title
            }
        }
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
