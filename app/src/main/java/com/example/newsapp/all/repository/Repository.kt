package com.example.newsapp.all.repository

import androidx.lifecycle.LiveData
import com.example.newsapp.all.api.NewsAPI
import com.example.newsapp.all.database.NewsDAO
import com.example.newsapp.all.models.Article
import javax.inject.Inject

class Repository @Inject constructor(private val apiService : NewsAPI, private val newsDAO: NewsDAO) {

    suspend fun getBreakingNews(countryCode : String,pageNumber : Int) =
        apiService.getBreakingNews(countryCode,pageNumber)

    suspend fun searchNews(searchQuery : String,pageNumber: Int) = apiService.searchNews(searchQuery)

    suspend fun upsert(article: Article) = newsDAO.upsert(article)

    suspend fun delete(article: Article) = newsDAO.deleteNews(article)

    suspend fun deleteAll() = newsDAO.deleteAllNews()

    fun getSavedNews() : LiveData<List<Article>> = newsDAO.getSavedNews()

    fun searchArticle(query: String) : LiveData<List<Article>> = newsDAO.searchItem(query)





}