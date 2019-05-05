package com.arctouch.codechallenge.scenes.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.dpToPx
import com.arctouch.codechallenge.core.gone
import com.arctouch.codechallenge.core.visible
import com.arctouch.codechallenge.util.COLUMN_NUMBER
import kotlinx.android.synthetic.main.fragment_home.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeFragment : Fragment() {

    private val homeViewModel: HomeViewModel by viewModel()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        configLayout()
        configObservers()

        homeViewModel.handleUpcomingMovies()
    }

    private fun configLayout() {
        recyclerView.apply {
            adapter = HomeAdapter(ArrayList())
            layoutManager = GridLayoutManager(context, COLUMN_NUMBER)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)

                    val isItemOnLeft = getChildAdapterPosition(view) % 2 == 0
                    val sixteenDp = 16.dpToPx(resources)
                    val fourDp = 4.dpToPx(resources)

                    outRect.apply {
                        left = if (isItemOnLeft) sixteenDp else fourDp
                        right = if (isItemOnLeft) fourDp else sixteenDp
                        top = fourDp
                        bottom = fourDp
                    }
                }
            })
        }
    }

    private fun configObservers() {
        homeViewModel.apply {
            loadingMovies.observe(this@HomeFragment, Observer { isLoading ->
                isLoading?.let {
                    with(progressBar) { if (it) visible() else gone() }
                }
            })

            moviesList.observe(this@HomeFragment, Observer { movieList ->
                movieList?.let {
                    (recyclerView.adapter as? HomeAdapter)?.swapMovies(it)
                }
            })
        }
    }

}
