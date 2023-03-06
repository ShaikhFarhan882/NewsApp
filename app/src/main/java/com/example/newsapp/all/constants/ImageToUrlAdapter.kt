package com.example.newsapp.all.constants

import android.widget.ImageView
import androidx.core.net.toUri
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

@BindingAdapter("urlToImage")
fun ImageView.loadImage(url: String?) {
    url?.let {
            val imgUri = it.toUri().buildUpon().scheme("https").build()
        Glide.with(this.context)
                .load(imgUri)
                .into(this)
    }
}