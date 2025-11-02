package com.linkan.newsfeedapp.ui.fragments

import android.os.Bundle
import android.view.View
import android.widget.Toast
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

    @Inject lateinit var newsAdapter: NewsAdapter

    private var mBinding: FragmentNewsFeedBinding? = null
    private val mViewModel: MainViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mViewModel.searchNewsForEverythingByKey("News")
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = FragmentNewsFeedBinding.bind(view)
        collectFlows()
        initRecyclerView()
    }

    private fun initRecyclerView() {
        mBinding?.apply {
            rvNewsFeeds.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
            rvNewsFeeds.adapter = newsAdapter

            newsAdapter.navigateOnItemClickListener { item : NewsArticle ->
                findNavController().navigate(NewsFeedFragmentDirections.actionNewsFeedFragmentToNewsFeedDetailFragment(item))
            }
        }
    }

    private fun collectFlows() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.newsFeedState
                    .collect { result ->
                    when (result) {
                        is ResultEvent.Loading -> {
                            // start loader
                            mBinding?.progressBar?.visibility = View.VISIBLE
                        }
                        is ResultEvent.Success -> {
                            // dismiss loader
                            mBinding?.progressBar?.visibility = View.GONE
                            newsAdapter.stringList = result.data
                        }
                        is ResultEvent.Error -> {}
                    }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                mViewModel.errorState.collect { result ->
                    // dismiss loader
                    mBinding?.progressBar?.visibility = View.GONE
                    Toast.makeText(requireContext(), "Error - $result", Toast.LENGTH_SHORT).show()
                }
            }
        }


    }


    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}