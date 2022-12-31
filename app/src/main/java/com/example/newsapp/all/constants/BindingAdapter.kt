package com.example.newsapp.all.constants

import android.widget.TextView
import androidx.databinding.BindingAdapter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@BindingAdapter("formattedDateAndTime")
fun TextView.formatDate(date : String){
    var outputDate: String? = null
    try {
        val curFormater = SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss")
        val postFormater = SimpleDateFormat("MMM dd, yyyy")

        val dateObj = curFormater.parse(date)
        outputDate = postFormater.format(dateObj)
        this.setText(outputDate)

    } catch (e: ParseException) {
        e.printStackTrace()
    }
}