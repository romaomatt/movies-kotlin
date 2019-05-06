package com.arctouch.codechallenge.scenes.main

import android.app.SearchManager
import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.onNavDestinationSelected
import androidx.navigation.ui.setupActionBarWithNavController
import com.arctouch.codechallenge.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : AppCompatActivity() {

    private val mainViewModel: MainViewModel by viewModel()
    private val navController: NavController by lazy { findNavController(R.id.navHostFragment) }
    private var searchView: SearchView? = null
    private var menuItem: MenuItem? = null

    private val _queryText = MutableLiveData<String>()
    val queryText: LiveData<String> = _queryText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        displayCachedQuery()
        setSupportActionBar(toolbar)
        setupActionBarWithNavController(navController)
    }

    override fun onSupportNavigateUp(): Boolean {
        return findNavController(R.id.navHostFragment).navigateUp()
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        navController.currentDestination?.let {
            menu?.findItem(R.id.searchFragment)?.apply {
                when (it.id) {
                    R.id.detailsFragment -> {
                        isVisible = false
                    }
                    R.id.homeFragment -> {
                        mainViewModel.clearCache()
                        isVisible = true
                    }
                    else -> {
                        mainViewModel.cachedQuery.value?.let {
                            displaySearchViewWithCache(it)
                        }
                    }
                }
            }
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        menuItem = menu?.findItem(R.id.searchFragment)
        searchView = menuItem?.actionView as? SearchView

        menuItem?.setOnActionExpandListener(object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem?): Boolean {
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem?): Boolean {
                navController.popBackStack()
                return true
            }
        })

        searchView?.apply {
            setSearchableInfo(searchManager.getSearchableInfo(componentName))
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String?): Boolean {
                    mainViewModel.updateCachedValue(query)
                    _queryText.postValue(query)
                    return false
                }

                override fun onQueryTextChange(newText: String?): Boolean {
                    mainViewModel.updateCachedValue(newText)
                    _queryText.postValue(newText)
                    return false
                }
            })
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }

    private fun displayCachedQuery() {
        mainViewModel.cachedQuery.value?.let {
            if (it.isNotBlank()) displaySearchViewWithCache(it)
        }
    }

    private fun displaySearchViewWithCache(query: String?) {
        menuItem?.expandActionView()
        searchView?.setQuery(query, false)
    }

}