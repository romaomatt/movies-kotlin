package com.arctouch.codechallenge.scenes.search

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.navigation.navOptions
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.configureGridMargins
import com.arctouch.codechallenge.core.dpToPx
import com.arctouch.codechallenge.core.gone
import com.arctouch.codechallenge.core.visible
import com.arctouch.codechallenge.data.MovieListStateEnum
import com.arctouch.codechallenge.scenes.main.MainActivity
import com.arctouch.codechallenge.util.MOVIES_COLUMN_NUMBER
import kotlinx.android.synthetic.main.fragment_search.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class SearchFragment : Fragment() {

    private val searchViewModel: SearchViewModel by viewModel()
    private val searchAdapter by lazy { SearchAdapter(ArrayList()) }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        configLayout()
        configQueryObserver()
        configObservers()
    }

    private fun configLayout() {
        searchRV.apply {
            adapter = searchAdapter
            layoutManager = GridLayoutManager(context, MOVIES_COLUMN_NUMBER)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)
                    outRect.configureGridMargins(
                        getChildAdapterPosition(view) % 2 == 0,
                        16.dpToPx(resources),
                        8.dpToPx(resources)
                    )
                }
            })
        }
    }

    private fun configQueryObserver() {
        (activity as? MainActivity)?.queryText?.observe(viewLifecycleOwner, Observer {
            searchViewModel.searchMovie(it)
        })
    }

    private fun configObservers() {
        searchViewModel.apply {
            movieList.observe(viewLifecycleOwner, Observer { movieList ->
                movieList?.let {
                    (searchRV.adapter as? SearchAdapter)?.swapMovies(it)
                }
            })

            movieState.observe(viewLifecycleOwner, Observer { state ->
                when (state) {
                    MovieListStateEnum.COMPLETE -> {
                        searchErrorTXT.gone()
                        searchPB.gone()
                    }
                    MovieListStateEnum.LOADING_ADAPTER -> {
                        searchAdapter.swapMovies(ArrayList())
                        searchPB.visible()
                        searchErrorTXT.gone()
                    }
                    MovieListStateEnum.ERROR -> {
                        searchPB.gone()
                        searchErrorTXT.visible()
                    }
                }
            })
        }
    }

}