package ru.mtsteta.flixnet.detailinfo

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
import ru.mtsteta.flixnet.repo.MovieDto

private const val MOVIE_DTO_KEY = "Movie"

class DetailFragment : Fragment() {

    private var movieDetail: MovieDto? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        arguments?.run { movieDetail = getParcelable(MOVIE_DTO_KEY) }

        movieDetail?.run {

            val tvTitle = view.findViewById<TextView>(R.id.tvMovieTitle).apply {
                text = title
            }

            val tvInfo = view.findViewById<TextView>(R.id.tvDetailInfoText).apply {
                text = description
            }

            val tvAgeLimit = view.findViewById<TextView>(R.id.tvAgeLimit).apply {
                text = "$ageLimit+"
            }

            val tvGenre = view.findViewById<TextView>(R.id.tvDetailGenre).apply {
                text = genre
            }

            val rbMovie = view.findViewById<RatingBar>(R.id.detailMovieRating).apply {
                rating = rateScore.toFloat()
            }

            val imgPoster = view.findViewById<ShapeableImageView>(R.id.imgPoster).apply {
                load(imageUrl.toUri())
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }

    companion object {
        @JvmStatic
        fun newInstance(movie: MovieDto): DetailFragment {
            val args = Bundle()
            args.putParcelable(MOVIE_DTO_KEY, movie)
            val fragment = DetailFragment()
            fragment.arguments = args
            return fragment
        }
    }
}