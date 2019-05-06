package com.arctouch.codechallenge.scenes.home

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.gone
import com.arctouch.codechallenge.core.visible
import com.arctouch.codechallenge.data.MovieListStateEnum
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
import kotlinx.android.synthetic.main.item_movie_footer.view.*

class HomeAdapter : PagedListAdapter<Movie, RecyclerView.ViewHolder>(MOVIE_COMPARATOR) {

    companion object {
        const val MOVIES_TYPE = 135
        const val FOOTER_TYPE = 951

        private val MOVIE_COMPARATOR = object : DiffUtil.ItemCallback<Movie>() {
            override fun areItemsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Movie, newItem: Movie): Boolean {
                return oldItem == newItem
            }
        }
    }

    private var currentState = MovieListStateEnum.LOADING

    override fun getItemViewType(position: Int): Int {
        return if (position < super.getItemCount()) MOVIES_TYPE else FOOTER_TYPE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                if (viewType == MOVIES_TYPE) R.layout.item_movie else R.layout.item_movie_footer,
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (getItemViewType(position) == MOVIES_TYPE) {
            getItem(position)?.let { movie ->
                holder.itemView.apply {
                    Glide.with(this)
                        .load(movie.posterPath?.let { POSTER_URL + it })
                        .centerCrop()
                        .transition(DrawableTransitionOptions.withCrossFade(200))
                        .listener(object: RequestListener<Drawable?> {
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
                        val direction = HomeFragmentDirections.showMovieDetailsFromHome(movie)
                        it.findNavController().navigate(direction)
                    }
                }
            }
        } else {
            holder.itemView.apply {
                with(footerPB) { if (currentState == MovieListStateEnum.LOADING) visible() else gone() }
                with(footerErrorTXT) { if (currentState == MovieListStateEnum.ERROR) visible() else gone() }
            }
        }
    }

    override fun getItemCount() = super.getItemCount() + if (showingFooter()) 1 else 0

    private fun showingFooter(): Boolean {
        return super.getItemCount() != 0 && (currentState == MovieListStateEnum.LOADING || currentState == MovieListStateEnum.ERROR)
    }

    fun updateState(state: MovieListStateEnum) {
        this.currentState = state
        notifyItemChanged(super.getItemCount())
    }
}
