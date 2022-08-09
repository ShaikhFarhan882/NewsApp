package com.example.newsapp.all.database

import androidx.room.TypeConverter
import com.example.newsapp.all.models.Source

class Converter {

    @TypeConverter
    fun fromSourceToString(source: Source) : String{
        return source.name
    }

    @TypeConverter
    fun toStringToSource(name : String) : Source{
        return Source(name,name)
    }
}