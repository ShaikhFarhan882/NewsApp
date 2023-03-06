package com.example.newsapp.all.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkCapabilities.TRANSPORT_CELLULAR
import android.net.NetworkCapabilities.TRANSPORT_WIFI
import android.util.Log
import androidx.lifecycle.*
import com.example.newsapp.all.constants.GetContext
import com.example.newsapp.all.constants.Resource
import com.example.newsapp.all.models.Article
import com.example.newsapp.all.models.NewsResponse
import com.example.newsapp.all.repository.Repository
import com.example.newsapp.all.ui.BreakingNews
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(private val repository: Repository,application: Application) : AndroidViewModel(application) {

    //Network Operations
    val breakingNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var breakingNewsPage = 1

    var breakingNewsAllResponse : NewsResponse? = null


    val searchNews : MutableLiveData<Resource<NewsResponse>> = MutableLiveData()

    var searchNewsPage = 1

     init {
       getBreakingNews("us")
   }


    fun getBreakingNews(countryCode : String) = viewModelScope.launch {
        /*breakingNews.postValue(Resource.Loading())*/
        safeCallBreakingNews(countryCode)
        /*val response = repository.getBreakingNews(countryCode,breakingNewsPage)
        breakingNews.postValue(HandleNewsResponse(response))*/
    }

    fun searchNews(searchQuery : String) = viewModelScope.launch {
        /*searchNews.postValue(Resource.Loading())*/
        safeCallSearchNews(searchQuery)
        /*searchNews.postValue(Resource.Loading())
        val response = repository.searchNews(searchQuery,searchNewsPage)
        searchNews.postValue(HandleSearchResponse(response))*/
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
                return Resource.Success(it)
            }
        }
        return Resource.Error(response.message())
    }


    private suspend fun safeCallBreakingNews(countryCode: String){
        breakingNews.postValue(Resource.Loading())
        try{
            if(checkForConnectivity()){
                val response = repository.getBreakingNews(countryCode,breakingNewsPage)
                breakingNews.postValue(HandleNewsResponse(response))
            }
            else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (exception : IOException){

            when(exception){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Data Conversion Error"))
            }

        }
    }



    private suspend fun safeCallSearchNews(searchQuery: String){
        searchNews.postValue(Resource.Loading())
        try{
            if(checkForConnectivity()){
                val response = repository.searchNews(searchQuery,searchNewsPage)
                searchNews.postValue(HandleSearchResponse(response))
            }
            else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }

        } catch (exception : Throwable){
            when(exception){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Data Conversion Error"))
            }

        }
    }



   //Room Operations
    fun upsert(article: Article) = viewModelScope.launch {
        repository.upsert(article)
    }

    fun delete(article: Article) = viewModelScope.launch {
        repository.delete(article)
    }

    fun deleteAll() = viewModelScope.launch {
        repository.deleteAll()
    }

    fun getSavedNews() = repository.getSavedNews()

    fun searchDatabase(query : String) : LiveData<List<Article>> {
        return repository.searchArticle(query)
    }


    //Internet Connectivity Conformation
     fun checkForConnectivity() : Boolean{
        var result = false
        val connectivityManager = getApplication<GetContext>().applicationContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager?

        connectivityManager?.let {
            it.getNetworkCapabilities(connectivityManager.activeNetwork)?.apply {
                result = when{
                    hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                    hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                    else->false
                }
            }
        }
        return result
    }


}

