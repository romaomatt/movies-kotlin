package com.arctouch.codechallenge.scenes.home

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.arctouch.codechallenge.R
import com.arctouch.codechallenge.core.gone
import com.arctouch.codechallenge.core.visible
import kotlinx.android.synthetic.main.home_activity.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class HomeActivity : AppCompatActivity() {

    private val viewModel: HomeViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_activity)

        configLayout()
        configObservers()

        viewModel.handleUpcomingMovies()
    }

    private fun configLayout() {
        recyclerView.adapter = HomeAdapter(ArrayList())
    }

    private fun configObservers() {
        viewModel.apply {
            loadingMovies.observe(this@HomeActivity, Observer { isLoading ->
                with(progressBar) { if (isLoading) visible() else gone() }
            })

            moviesList.observe(this@HomeActivity, Observer {
                (recyclerView.adapter as? HomeAdapter)?.addNewMovies(it)
            })
        }
    }

}
