package com.arctouch.codechallenge.scenes.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.decimalToInt
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.BACKDROP_URL
import com.arctouch.codechallenge.util.POSTER_URL
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.fragment_details.*

class DetailsFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_details, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arguments?.let {
            val safeArgs = DetailsFragmentArgs.fromBundle(it)
            val movie = safeArgs.movie
            displayMovieDetails(movie)
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
                adapter = DetailsGenreAdapter(movie.genres?.map { it.name } ?: ArrayList())
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            }

            Glide.with(this@DetailsFragment)
                .load(posterPath?.let { POSTER_URL + it })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(posterIMG)

            Glide.with(this@DetailsFragment)
                .load(backdropPath?.let { BACKDROP_URL + it })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(backdropIMG)
        }
    }

}