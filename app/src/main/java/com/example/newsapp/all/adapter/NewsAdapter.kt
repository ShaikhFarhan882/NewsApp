package com.example.newsapp.all.adapter


import android.view.LayoutInflater

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.newsapp.R
import com.example.newsapp.all.models.Article
import com.example.newsapp.databinding.SingleRowBinding

class NewsAdapter: ListAdapter<Article, NewsAdapter.ArticleViewHolder>(TaskDiffCallBack) {

    companion object TaskDiffCallBack : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }


        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem == newItem
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ArticleViewHolder {
        return ArticleViewHolder(SingleRowBinding.inflate(LayoutInflater.from(parent.context),
            parent,
            false))
    }

    override fun onBindViewHolder(holder: ArticleViewHolder, position: Int) {
        val article = getItem(position)
        holder.Bind(article)

        Glide.with(holder.binding.newsImage.context.applicationContext).load(article.urlToImage)
            .placeholder(
                R.drawable.ic_no_image).into(
            holder.binding.newsImage
        )

        holder.binding.root.setOnClickListener {
            onItemClickListener?.let {
                it(article)
            }
        }

    }

    private var onItemClickListener : ((Article) -> Unit)? = null

    fun setOnItemClickListener(listener : (Article) -> Unit){
        onItemClickListener = listener
    }



    class ArticleViewHolder(val binding: SingleRowBinding) : RecyclerView.ViewHolder(binding.root) {

        fun Bind(article: Article) {
            binding.data = article
            binding.executePendingBindings()
        }
    }



}