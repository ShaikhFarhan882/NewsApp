package com.example.newsapp.all.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.all.api.RetrofitInstance
import com.example.newsapp.all.database.NewsDAO
import com.example.newsapp.all.database.NewsDatabase
import com.example.newsapp.all.models.Article

class Repository(private val newsDatabase: NewsDatabase) {

    suspend fun getBreakingNews(countryCode : String,pageNumber : Int) =
        RetrofitInstance.api.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String,pageNumber: Int) = RetrofitInstance.api.searchNews(searchQuery)

    suspend fun upsert(article: Article) = newsDatabase.newsDAO().upsert(article)

    suspend fun delete(article: Article) = newsDatabase.newsDAO().deleteNews(article)

    suspend fun deleteAll() = newsDatabase.newsDAO().deleteAllNews()

    fun getSavedNews() : LiveData<List<Article>> = newsDatabase.newsDAO().getSavedNews()




}