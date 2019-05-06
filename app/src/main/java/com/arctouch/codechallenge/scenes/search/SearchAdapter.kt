package com.arctouch.codechallenge.scenes.search

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.gone
import com.arctouch.codechallenge.core.visible
import com.arctouch.codechallenge.model.Movie
import com.arctouch.codechallenge.util.BaseViewHolder
import com.arctouch.codechallenge.util.POSTER_URL
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_movie.view.*

class SearchAdapter(private val movieList: ArrayList<Movie>) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_movie,
                parent,
                false
            )
        )
    }

    override fun getItemCount() = movieList.count()

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        val movie = movieList[position]

        holder.itemView.apply {
            Glide.with(this)
                .load(movie.posterPath?.let { POSTER_URL + it })
                .centerCrop()
                .transition(DrawableTransitionOptions.withCrossFade(200))
                .listener(object : RequestListener<Drawable?> {
                    override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable?>?, isFirstResource: Boolean): Boolean {
                        movieNameTXT.apply {
                            visible()
                            text = movie.title
                        }
                        return false
                    }

                    override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable?>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                        movieNameTXT.gone()
                        return false
                    }
                })
                .error(R.drawable.ic_image_placeholder)
                .into(posterImageView)

            setOnClickListener {
                val direction = SearchFragmentDirections.showMovieDetailsFromSearch(movie)
                it.findNavController().navigate(direction)
            }
        }
    }

    fun swapMovies(newMovies: ArrayList<Movie>) {
        movieList.apply {
            clear()
            addAll(newMovies)
        }
        notifyDataSetChanged()
    }
}