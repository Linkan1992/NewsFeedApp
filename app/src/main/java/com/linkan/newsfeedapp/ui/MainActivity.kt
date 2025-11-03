package com.linkan.newsfeedapp.ui

import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupActionBarWithNavController
import com.linkan.newsfeedapp.R
import com.linkan.newsfeedapp.databinding.ActivityMainBinding
import com.linkan.newsfeedapp.ui.fragments.NewsFeedFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    private var searchMenuItemVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        configureActionBar()
    }

    private fun configureActionBar() {

        setSupportActionBar(mBinding.toolbar)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        appBarConfiguration = AppBarConfiguration(navController.graph)

        setupActionBarWithNavController(navController, appBarConfiguration)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            supportActionBar?.title = destination.label
            // Show search only on NewsFeedFragment
            searchMenuItemVisible = destination.id == R.id.newsFeedFragment
            invalidateOptionsMenu()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_search, menu)
        val searchMenuItem = menu.findItem(R.id.action_search)
        searchMenuItem.isVisible = searchMenuItemVisible

        if (searchMenuItemVisible) {
            val searchView = searchMenuItem.actionView as androidx.appcompat.widget.SearchView
            setupSearchView(searchView)
        }

        return true
    }

    private fun setupSearchView(searchView: androidx.appcompat.widget.SearchView) {
        searchView.queryHint = "Search news..."
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                query?.let {
                    sendSearchQueryToFragment(it)
                    searchView.clearFocus()
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }

    private fun sendSearchQueryToFragment(query: String) {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val fragment = navHostFragment.childFragmentManager.primaryNavigationFragment
        if (fragment is NewsFeedFragment) {
            fragment.onSearchQuery(query)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return NavigationUI.navigateUp(navController, appBarConfiguration) || super.onSupportNavigateUp()
    }
}