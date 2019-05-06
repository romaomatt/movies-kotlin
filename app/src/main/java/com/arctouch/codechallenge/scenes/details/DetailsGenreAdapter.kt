package com.arctouch.codechallenge.scenes.details

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.model.Genre
import com.arctouch.codechallenge.util.BaseViewHolder
import kotlinx.android.synthetic.main.item_genre.view.*

class DetailsGenreAdapter(private val genreList: List<Genre>) : RecyclerView.Adapter<BaseViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseViewHolder {
        return BaseViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_genre,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = genreList.count()

    override fun onBindViewHolder(holder: BaseViewHolder, position: Int) {
        holder.itemView.genreTXT.text = genreList[position].name
    }

}