package com.example.newsapp.all.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.newsapp.all.constants.Resource
import com.example.newsapp.all.models.Article
import com.example.newsapp.all.models.NewsResponse
import com.example.newsapp.all.repository.Repository
import com.example.newsapp.all.ui.BreakingNews
import kotlinx.coroutines.launch
import retrofit2.Response

class NewsViewModel(private val repository: Repository) : ViewModel() {

    init {
        getBreakingNews("in")
    }

    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var breakingNewsPage = 1

    var breakingNewsAllResponse : NewsResponse? = null


    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var searchNewsPage = 1

    var searchNewsAllResponse : NewsResponse? = null

    fun getBreakingNews(countryCode : String) = viewModelScope.launch {

        val response = repository.getBreakingNews(countryCode,breakingNewsPage)

        breakingNews.postValue(HandleNewsResponse(response))
    }

    fun searchNews(searchQuery : String) = viewModelScope.launch {
        searchNews.postValue(Resource.Loading())
        val response = repository.searchNews(searchQuery,searchNewsPage)
        searchNews.postValue(HandleSearchResponse(response))
    }

    private fun HandleNewsResponse(response : Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                breakingNewsPage++
                if(breakingNewsAllResponse==null){
                    breakingNewsAllResponse = it
                }
                else{
                    val oldArticles = breakingNewsAllResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(breakingNewsAllResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }

    private fun HandleSearchResponse(response : Response<NewsResponse>) : Resource<NewsResponse> {
        if(response.isSuccessful){
            response.body()?.let {
                searchNewsPage++
                if(searchNewsAllResponse==null){
                    searchNewsAllResponse = it
                }
                else{
                    val oldArticles = searchNewsAllResponse?.articles
                    val newArticles = it.articles
                    oldArticles?.addAll(newArticles)
                }
                return Resource.Success(searchNewsAllResponse ?: it)
            }
        }
        return Resource.Error(response.message())
    }


    fun upsert(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    fun delete(article: Article) = viewModelScope.launch {
        repository.delete(article)
    }

    fun getSavedNews() = repository.getSavedNews()



}