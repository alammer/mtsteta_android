package ru.mtsteta.flixnet.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.movies.MovieDto

class DetailFragment : Fragment() {

    var  movieDetail : MovieDto? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_movie_details, container, false)

        arguments.let { movieDetail = it?.getParcelable("Movie") }



        val tvTitle = view.findViewById<TextView>(R.id.tvMovieTitle)?.apply {
            text = movieDetail?.title
        }

        val tvInfo = view.findViewById<TextView>(R.id.tvDetailInfoText)?.apply {
            text = movieDetail?.description
        }

        val tvAgeLimit = view.findViewById<TextView>(R.id.tvAgeLimit)?.apply {
            text = "${movieDetail?.ageLimit.toString()}+"
        }

        val tvGenre = view.findViewById<TextView>(R.id.tvDetailGenre)?.apply {
            text = movieDetail?.genre
        }

        val rbMovie = view.findViewById<RatingBar>(R.id.detailMovieRating)?.apply {
            rating = movieDetail?.rateScore?.toFloat() ?: 0f
        }

        val imgPoster = view.findViewById<ShapeableImageView>(R.id.imgPoster)?.apply {
            load(movieDetail?.imageUrl?.toUri())
            scaleType = ImageView.ScaleType.CENTER_CROP
        }

        return view
    }

    companion object {
        fun newInstance(movie: MovieDto): DetailFragment {
            val args = Bundle()
            args.putParcelable("Movie", movie)
            val fragment = DetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}