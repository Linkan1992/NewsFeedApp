package com.linkan.newsfeedapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import com.linkan.newsfeedapp.databinding.ItemErrorRetryBinding
import com.linkan.newsfeedapp.databinding.ItemNewsFeedRowBinding
import com.linkan.newsfeedapp.databinding.ItemPaginationLoaderBinding
import com.linkan.newsfeedapp.domain.model.NewsArticle
import javax.inject.Inject

class NewsAdapter @Inject constructor(
    private val glide: RequestManager
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var showLoader = false
    private var showError = false
    private var errorMessage: String? = null
    private var onRetryClick: (() -> Unit)? = null
    private var onItemClickListener: ((NewsArticle) -> Unit)? = null

    fun navigateOnItemClickListener(listener: (NewsArticle) -> Unit) {
        onItemClickListener = listener
    }

    fun setOnRetryClickListener(listener: () -> Unit) {
        onRetryClick = listener
    }

    private val diffCallback = object : DiffUtil.ItemCallback<NewsArticle>() {
        override fun areItemsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem.imageUrl == newItem.imageUrl
        }

        override fun areContentsTheSame(oldItem: NewsArticle, newItem: NewsArticle): Boolean {
            return oldItem == newItem
        }
    }

    private val recyclerDiffList = AsyncListDiffer(this, diffCallback)

    var newsList : List<NewsArticle>
        get() = recyclerDiffList.currentList
        set(value) = recyclerDiffList.submitList(value)

    fun showPagingLoader(show: Boolean) {
        if (showLoader == show) return
        val lastPosition = itemCount
        showLoader = show
        showError = false
        if (show) {
            notifyItemInserted(lastPosition)
        } else {
            notifyItemRemoved(lastPosition - 1)
        }
    }

    fun showPagingError(errorMessage: String? = null) {
        if (showError) return
        this.errorMessage = errorMessage
        val lastPosition = itemCount
        showError = true
        showLoader = false
        notifyItemInserted(lastPosition)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            TYPE_ITEM -> {
                val binding = ItemNewsFeedRowBinding.inflate(inflater, parent, false)
                NewsViewHolder(binding)
            }
            TYPE_LOADER -> {
                val binding = ItemPaginationLoaderBinding.inflate(inflater, parent, false)
                LoaderViewHolder(binding)
            }
            TYPE_ERROR -> {
                val binding = ItemErrorRetryBinding.inflate(inflater, parent, false)
                ErrorViewHolder(binding)
            }
            else -> throw IllegalArgumentException("Unknown viewType: $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is NewsViewHolder -> {
                val item = recyclerDiffList.currentList[position]
                glide.load(item.imageUrl).into(holder.binding.imvArticleImage)
                holder.binding.mtvArticleTitle.text = item.title
                holder.binding.mtvArticleDescription.text = item.description
                holder.itemView.setOnClickListener { onItemClickListener?.invoke(item) }
            }

            is LoaderViewHolder -> { }

            is ErrorViewHolder -> {
                holder.binding.mtvErrorMsg.text = errorMessage ?: "Something went wrong!"
                holder.binding.btnRetry.setOnClickListener { onRetryClick?.invoke() }
            }
        }
    }

    inner class NewsViewHolder(val binding: ItemNewsFeedRowBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class LoaderViewHolder(val binding: ItemPaginationLoaderBinding) :
        RecyclerView.ViewHolder(binding.root)

    inner class ErrorViewHolder(val binding: ItemErrorRetryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        val baseCount = recyclerDiffList.currentList.size
        return when {
            showError -> baseCount + 1
            showLoader -> baseCount + 1
            else -> baseCount
        }
    }

    override fun getItemViewType(position: Int): Int {
        val baseCount = recyclerDiffList.currentList.size
        return when {
            showError && position == baseCount -> TYPE_ERROR
            showLoader && position == baseCount -> TYPE_LOADER
            else -> TYPE_ITEM
        }
    }

    companion object {
        private const val TYPE_ITEM = 0
        private const val TYPE_LOADER = 1
        private const val TYPE_ERROR = 2
    }
}