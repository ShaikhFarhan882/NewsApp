package com.example.newsapp.all.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.newsapp.all.models.Article

@Dao
interface NewsDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(article: Article) : Long

    @Query("SELECT * FROM articles")
    fun getSavedNews() : LiveData<List<Article>>

    @Delete
    suspend fun deleteNews(article: Article)

    @Query("DELETE FROM articles")
    suspend fun deleteAllNews()

    @Query("SELECT * FROM articles WHERE title LIKE :query")
    fun searchItem(query : String) : LiveData<List<Article>>

}