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
import androidx.navigation.fragment.navArgs
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.repo.MovieDto

class DetailFragment : Fragment() {

    private var movieDetail: MovieDto? = null

    private val args: DetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {


        args.movieItem.run {

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
                load(imageUrl.toUri()){
                    error(R.drawable.broken_image)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }

        super.onViewCreated(view, savedInstanceState)
    }
}