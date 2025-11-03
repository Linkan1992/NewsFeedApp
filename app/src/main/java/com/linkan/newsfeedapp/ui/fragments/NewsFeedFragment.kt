package com.linkan.newsfeedapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.linkan.newsfeedapp.R
import com.linkan.newsfeedapp.databinding.FragmentNewsFeedBinding
import com.linkan.newsfeedapp.domain.model.NewsArticle
import com.linkan.newsfeedapp.ui.MainViewModel
import com.linkan.newsfeedapp.ui.NewsAdapter
import com.linkan.newsfeedapp.util.ResultEvent
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class NewsFeedFragment : Fragment(R.layout.fragment_news_feed) {

    @Inject
    lateinit var newsAdapter: NewsAdapter
    private var mBinding: FragmentNewsFeedBinding? = null
    private val mViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = FragmentNewsFeedBinding.bind(view)
        collectFlows()
        setupRetryButton()
        initRecyclerView()
    }

    fun onSearchQuery(query: String) {
        mBinding?.apply {
            // Check if this is actually a new query
            val isNewQuery = mViewModel.currentSearchKey != query.trim()
            if (isNewQuery) {
                showErrorLayout(false)
                mViewModel.searchNewKeyword(query)
            }
        }
    }

    private fun initRecyclerView() {
        mBinding?.apply {
            rvNewsFeeds.apply {
                layoutManager = LinearLayoutManager(requireContext())
                adapter = newsAdapter

                addOnScrollListener(object : RecyclerView.OnScrollListener() {
                    override fun onScrolled(rv: RecyclerView, dx: Int, dy: Int) {
                        super.onScrolled(rv, dx, dy)
                        if (dy <= 0) return
                        val layoutManager = rv.layoutManager as LinearLayoutManager
                        val visibleItemCount = layoutManager.childCount
                        val totalItemCount = layoutManager.itemCount
                        val firstVisibleItemPos = layoutManager.findFirstVisibleItemPosition()

                        val shouldPaginate =
                            !mViewModel.isLoading &&
                                    !mViewModel.isPaginating &&
                                    !mViewModel.isLastPage &&
                                    (visibleItemCount + firstVisibleItemPos >= totalItemCount - 5) &&
                                    firstVisibleItemPos >= 0

                        if (shouldPaginate) {
                            mViewModel.loadNextPage()
                        }
                    }
                })
            }

            newsAdapter.navigateOnItemClickListener { item ->
                findNavController().navigate(
                    NewsFeedFragmentDirections.actionNewsFeedFragmentToNewsFeedDetailFragment(item)
                )
            }

            newsAdapter.setOnRetryClickListener {
                mViewModel.retry()
            }
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.newsFeedState.collect { result ->
                    when (result) {
                        is ResultEvent.Loading -> {
                            if (mViewModel.isPaginating) {
                                newsAdapter.showPagingLoader(true)
                            } else {
                                showMainLoader(true)
                            }
                        }

                        is ResultEvent.Success -> {
                            val list = result.data
                            showMainLoader(false)
                            newsAdapter.showPagingLoader(false)
                            val message = if(list.isEmpty()) "No Data Found" else "Something went wrong!"
                            showErrorLayout(show = list.isEmpty(), message = message, visibleRetryButton = list.isNotEmpty())
                            newsAdapter.newsList = list
                        }

                        is ResultEvent.Error -> {
                            showMainLoader(false)
                            if (mViewModel.currentPage == 1)
                                showErrorLayout(true, result.errorMessage)
                            else
                                newsAdapter.showPagingError(result.errorMessage)
                        }
                    }
                }
            }
        }
    }

    private fun setupRetryButton() {
        mBinding?.apply {
            btnRetry.setOnClickListener {
                showErrorLayout(false)
                mViewModel.retry()
            }
        }
    }

    private fun showMainLoader(show: Boolean) {
        mBinding?.progressBar?.visibility = if (show) View.VISIBLE else View.GONE
    }

    private fun showErrorLayout(
        show: Boolean,
        message: String? = "Something went wrong",
        visibleRetryButton: Boolean = true
    ) {
        mBinding?.apply {
            errorLayout.visibility = if (show) View.VISIBLE else View.GONE
            tvError.text = message
            btnRetry.visibility = if (visibleRetryButton) View.VISIBLE else View.GONE
        }
    }

    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}