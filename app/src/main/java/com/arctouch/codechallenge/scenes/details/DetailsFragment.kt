package com.arctouch.codechallenge.scenes.details

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.decimalToInt
import com.arctouch.codechallenge.core.gone
import com.arctouch.codechallenge.core.visible
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.BACKDROP_URL
import com.arctouch.codechallenge.util.POSTER_URL
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.fragment_details.*
import org.koin.androidx.viewmodel.ext.android.getViewModel
import org.koin.core.parameter.parametersOf

class DetailsFragment : Fragment() {

    private lateinit var detailsViewModel: DetailsViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val safeArgs = DetailsFragmentArgs.fromBundle(it)
            val movie = safeArgs.movie

            detailsViewModel = getViewModel { parametersOf(movie.id) }

            configObservers()
            displayMovieDetails(movie)
        } ?: run {
            findNavController().popBackStack()
        }
    }

    private fun displayMovieDetails(movie: Movie) {
        val notInformed = getString(R.string.not_informed)

        movie.apply {
            (activity as? AppCompatActivity)?.supportActionBar?.title = movie.title

            releaseDateTXT.text = releaseDate ?: notInformed
            overviewTXT.text = overview ?: notInformed
            ratingTXT.text = voteAverage
            ratingPB.progress = voteAverage.decimalToInt()

            genresRV.apply {
                adapter = DetailsGenreAdapter(movie.genres ?: ArrayList())
                layoutManager = GridLayoutManager(context, 2, GridLayoutManager.HORIZONTAL, false)
            }

            Glide.with(this@DetailsFragment)
                .load(posterPath?.let { POSTER_URL + it })
                .placeholder(R.drawable.ic_image_placeholder)
                .into(posterIMG)

            Glide.with(this@DetailsFragment)
                .load(backdropPath?.let { BACKDROP_URL + it })
                .placeholder(R.drawable.ic_image_placeholder)
                .into(backdropIMG)
        }
    }

    private fun configObservers() {
        detailsViewModel.apply {
            loadingTrailer.observe(viewLifecycleOwner, Observer { isLoading ->
                isLoading?.let {
                    with(trailerPB) { if (it) visible() else gone() }
                    changeTrailerButton(it, false)
                }
            })

            trailer.observe(viewLifecycleOwner, Observer { trailerUrl ->
                trailerUrl?.let {
                    setTrailerClickListener(it, false)
                }
            })

            errorFound.observe(viewLifecycleOwner, Observer { foundError ->
                foundError?.let {
                    if (it) changeTrailerButton(false, it)
                }
            })
        }
    }

    private fun changeTrailerButton(isLoading: Boolean, foundError: Boolean) {
        when {
            isLoading -> displayTrailerButton("", false, R.drawable.shape_rectangle_pigment_indigo_10dp)
            !isLoading && !foundError -> displayTrailerButton(getString(R.string.play_trailer), true, R.drawable.shape_rectangle_pigment_indigo_10dp)
            foundError -> displayTrailerButton(getString(R.string.play_trailer), true, R.drawable.shape_rectangle_amethyst_10dp)
        }
    }

    private fun displayTrailerButton(label: String, displayDrawableStart: Boolean, backgroundResId: Int) {
        val drawableStart = if (displayDrawableStart) {
            ResourcesCompat.getDrawable(resources, R.drawable.ic_play, null)
        } else {
            null
        }

        val backgroundDrawable = ResourcesCompat.getDrawable(resources, backgroundResId, null)

        trailerTXT.apply {
            text = label
            setCompoundDrawablesWithIntrinsicBounds(drawableStart, null, null, null)
            background = backgroundDrawable
        }
    }

    private fun setTrailerClickListener(trailerUrl: String, foundError: Boolean) {
        if (!foundError) {
            trailerTXT.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(trailerUrl))
                startActivity(intent)
            }
        }
    }

}