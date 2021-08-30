package ru.mtsteta.flixnet.movieinfo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.core.view.size
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.imageview.ShapeableImageView
import ru.mtsteta.flixnet.BuildConfig
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.repo.MovieDto

class DetailFragment : Fragment() {

    private lateinit var tvTitle: TextView
    private lateinit var tvInfo: TextView
    private lateinit var tvAgeLimit: TextView
    private lateinit var chgGenres: ChipGroup
    private lateinit var tvRelease: TextView
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

        loadData(args.movieItem)

        detailViewModel.fetchActors(args.movieItem.movie_id)

        args.movieItem.genres?.let {
            detailViewModel.fetchGenres(it)
        }

        detailViewModel.actorList.observe(viewLifecycleOwner, {
            actorAdapter.submitList(it)
        })

        detailViewModel.genreList.observe(viewLifecycleOwner, {
            loadGenre(it)
        })
    }

    private fun initViews(view: View) {
        tvTitle = view.findViewById(R.id.tvMovieTitle)
        tvInfo = view.findViewById(R.id.tvDetailInfoText)
        tvAgeLimit = view.findViewById(R.id.tvAgeLimit)
        chgGenres = view.findViewById(R.id.chgGenres)
        tvRelease = view.findViewById(R.id.tvReleaseDate)
        rbMovie = view.findViewById(R.id.detailMovieRating)
        imgPoster = view.findViewById(R.id.imgPoster)
        actorRecycler = view.findViewById(R.id.rvActorsList)

        actorAdapter = ActorListAdapter()

        actorRecycler.adapter = actorAdapter

        actorRecycler.addItemDecoration(ActorSpaceItemDecoration(resources.getDimensionPixelSize(R.dimen.detailed_actor_rv_item_horozontal_space)))
    }

    private fun loadData(movie: MovieDto) {
        movie.run {
            tvTitle.text = title
            tvInfo.text = overview
            tvAgeLimit.text = ageLimit
            tvRelease.text = release_date
            rbMovie.rating = rateScore?.let {  it.toFloat() / 2.0f } ?: 0.0f
            imgPoster.apply {
                load(BuildConfig.BASE_BACK_URL + backdropUrl) {
                    error(R.drawable.broken_image)
                }
                scaleType = ImageView.ScaleType.CENTER_CROP
            }
        }
    }

    private fun loadGenre (genres: List<String>) {
        if (chgGenres.size > 0) return
        genres.forEach {
            val chip = Chip(requireContext())
            chip.text = it
            chgGenres.addView(chip as View)
        }
    }
}