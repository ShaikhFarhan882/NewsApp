package com.example.newsapp.all.di

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.newsapp.all.database.NewsDAO
import com.example.newsapp.all.database.NewsDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DatabaseModule {

    @Singleton
    @Provides
    fun providesDatabase(@ApplicationContext appContext: Context): NewsDatabase {
        return Room.databaseBuilder(
            appContext,
            NewsDatabase::class.java,
            "news_db"
        ).fallbackToDestructiveMigration()
            .build()
    }


    @Singleton
    @Provides
    fun providesDAO(newsDatabase : NewsDatabase) : NewsDAO{
        return newsDatabase.newsDAO()
    }

}