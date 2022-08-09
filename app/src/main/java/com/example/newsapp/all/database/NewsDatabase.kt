package com.example.newsapp.all.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.newsapp.all.models.Article

@Database(
    entities = [Article::class],
    version = 1,
    exportSchema = false,
)
@TypeConverters(Converter::class)
abstract class NewsDatabase : RoomDatabase(){
    abstract fun newsDAO() : NewsDAO

    companion object{
        @Volatile
        private var INSTANCE: NewsDatabase? = null

        fun getDatabase(context: Context): NewsDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        NewsDatabase::class.java,
                        "article_db"
                    ).fallbackToDestructiveMigration().build()
                    INSTANCE = instance
                }
                return instance
            }
        }

    }
}