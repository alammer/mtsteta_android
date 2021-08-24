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
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.imageview.ShapeableImageView
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.movies.MovieClickListener
import ru.mtsteta.flixnet.movies.MovieListAdapter
import ru.mtsteta.flixnet.movies.MoviesListFragmentDirections
import ru.mtsteta.flixnet.movies.MoviesListViewModel
import ru.mtsteta.flixnet.repo.MovieDto

class DetailFragment : Fragment() {

    private lateinit var tvTitle: TextView
    private lateinit var tvInfo: TextView
    private lateinit var tvAgeLimit: TextView
    private lateinit var tvGenre: TextView
    private lateinit var rbMovie: RatingBar
    private lateinit var imgPoster: ShapeableImageView
    private lateinit var actorRecycler: RecyclerView
    private lateinit var actorAdapter: ActorListAdapter

    private val args: DetailFragmentArgs by navArgs()

    private val detailViewModel: DetailViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_movie_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        detailViewModel.fetchActors(args.movieItem.movie_id)

        loadData(args.movieItem)

        detailViewModel.actorList.observe(viewLifecycleOwner, {
            actorAdapter.submitList(it)
        })
    }

    private fun initViews(view: View) {
        tvTitle = view.findViewById(R.id.tvMovieTitle)
        tvInfo = view.findViewById(R.id.tvDetailInfoText)
        tvAgeLimit = view.findViewById(R.id.tvAgeLimit)
        tvGenre = view.findViewById(R.id.tvDetailGenre)
        rbMovie = view.findViewById(R.id.detailMovieRating)
        imgPoster = view.findViewById(R.id.imgPoster)

        actorRecycler = view.findViewById(R.id.rvActorsList)

        actorAdapter = ActorListAdapter()

        actorRecycler.adapter = actorAdapter
    }

    private fun loadData(movie: MovieDto) {
        movie.run {
            tvTitle.text = title
            tvInfo.text = overview
            tvAgeLimit.text = ageLimit
            tvGenre.text = genres.toString()
            rbMovie.rating = rateScore.toFloat()
            imgPoster.apply {
                load(BuildConfig.BASE_BACK_URL + backdropUrl) {
                    error(R.drawable.broken_image)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }
}