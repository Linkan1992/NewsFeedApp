package com.linkan.newsfeedapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.linkan.newsfeedapp.databinding.ItemNewsFeedRowBinding
import com.linkan.newsfeedapp.domain.model.NewsArticle
import javax.inject.Inject

class NewsAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private var onItemClickListener: ((NewsArticle) -> Unit)? = null

    fun deleteOnItemClickListener(listener: (NewsArticle) -> Unit) {
        onItemClickListener = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerDiffList = AsyncListDiffer(this, diffCallback)

    var stringList: List<NewsArticle>
        get() = recyclerDiffList.currentList
        set(value) = recyclerDiffList.submitList(value)

    class NewsViewHolder(val mBinding: ItemNewsFeedRowBinding) :
        RecyclerView.ViewHolder(mBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        return NewsViewHolder(
            ItemNewsFeedRowBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {

        stringList[position].let { newsArticle ->
            glide.load(newsArticle.imageUrl)
                .into(holder.mBinding.imvArticleImage)
            holder.mBinding.mtvArticleTitle.text = newsArticle.title
            holder.mBinding.mtvArticleDescription.text = newsArticle.description
            holder.itemView.apply {
                setOnClickListener {
                    onItemClickListener?.let { listener ->
                        listener(newsArticle)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return stringList.size
    }
}