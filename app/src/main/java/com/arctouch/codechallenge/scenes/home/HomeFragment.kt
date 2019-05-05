package com.arctouch.codechallenge.scenes.home

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.dpToPx
import com.arctouch.codechallenge.core.gone
import com.arctouch.codechallenge.core.visible
import com.arctouch.codechallenge.util.MOVIES_COLUMN_NUMBER
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
    }

    private fun configLayout() {
        homeSRL.apply {
            setColorSchemeResources(R.color.colorAccent)
            setOnRefreshListener {
                homeViewModel.handleMovieList(true)
            }
        }

        homeRV.apply {
            adapter = HomeAdapter(ArrayList())
            layoutManager = GridLayoutManager(context, MOVIES_COLUMN_NUMBER)
            addItemDecoration(object : RecyclerView.ItemDecoration() {
                override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                    super.getItemOffsets(outRect, view, parent, state)

                    val isItemOnLeft = getChildAdapterPosition(view) % 2 == 0
                    val sixteenDp = 16.dpToPx(resources)
                    val eightDp = 8.dpToPx(resources)

                    outRect.apply {
                        left = if (isItemOnLeft) sixteenDp else eightDp
                        right = if (isItemOnLeft) eightDp else sixteenDp
                        top = eightDp
                        bottom = eightDp
                    }
                }
            })
        }
    }

    private fun configObservers() {
        homeViewModel.apply {
            loadingMovies.observe(viewLifecycleOwner, Observer { isLoading ->
                isLoading?.let {
                    with(homePB) { if (it) visible() else gone() }
                    with(homeSRL) { if (isRefreshing) isRefreshing = false }
                }
            })

            moviesList.observe(viewLifecycleOwner, Observer { movieList ->
                movieList?.let {
                    (homeRV.adapter as? HomeAdapter)?.swapMovies(it)
                }
            })

            foundError.observe(viewLifecycleOwner, Observer { foundError ->
                foundError?.let {
                    if (it) Toast.makeText(context, getString(R.string.default_error_message), Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

}
