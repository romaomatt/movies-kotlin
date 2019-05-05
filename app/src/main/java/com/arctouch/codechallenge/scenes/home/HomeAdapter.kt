package com.arctouch.codechallenge.scenes.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.BaseViewHolder
import com.arctouch.codechallenge.util.POSTER_URL
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import kotlinx.android.synthetic.main.item_movie.view.*

class HomeAdapter(private val movies: ArrayList<Movie>) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_movie,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = movies.count()

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val movie = movies[position]

        holder.itemView.apply {
            Glide.with(this)
                .load(movie.posterPath?.let { POSTER_URL + it })
                .apply(RequestOptions().placeholder(R.drawable.ic_image_placeholder))
                .into(posterImageView)

            setOnClickListener {
                val direction = HomeFragmentDirections.showMovieDetailsAction(movie)
                it.findNavController().navigate(direction)
            }
        }
    }

    fun swapMovies(newMovies: ArrayList<Movie>) {
        movies.apply {
            clear()
            addAll(newMovies)
        }
        notifyDataSetChanged()
    }
}
