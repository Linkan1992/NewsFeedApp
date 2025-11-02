package com.linkan.newsfeedapp.ui.fragments

import android.os.Bundle
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.RequestManager
import com.linkan.newsfeedapp.R
import com.linkan.newsfeedapp.databinding.FragmentNewsFeedDetailBinding
import com.linkan.newsfeedapp.ui.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class NewsFeedDetailFragment : Fragment(R.layout.fragment_news_feed_detail) {

    private var mBinding : FragmentNewsFeedDetailBinding? = null
    @Inject lateinit var glide : RequestManager

    private val mViewModel : MainViewModel by activityViewModels()

    private val args : NewsFeedDetailFragmentArgs by navArgs()


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        mBinding = FragmentNewsFeedDetailBinding.bind(view)
        initBackCallBack()
        setupUI()
    }

    private fun setupUI() {
            args.item?.let { newsArticle ->
                mBinding?.run {
                    mtvArticleTitle.text = newsArticle.title
                    mtvArticleDescription.text = newsArticle.description
                    glide.load(newsArticle.imageUrl).into(this.imvArticleImage)
                }
            }
    }

    private fun initBackCallBack() {

        val backCallBack = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                findNavController().popBackStack()
            }
        }

        requireActivity().onBackPressedDispatcher.addCallback(backCallBack)
    }


    override fun onDestroyView() {
        mBinding = null
        super.onDestroyView()
    }

}