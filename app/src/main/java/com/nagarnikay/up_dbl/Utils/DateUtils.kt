package com.nagarnikay.up_dbl.Utils



import java.text.SimpleDateFormat
import java.util.*

object DateUtils {
    @JvmStatic
    fun getCurrentDateAsString(): String {
        // Get the current date
        val currentDate = Date()

        // Define the date format
        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

        // Format the date as a string
        return dateFormat.format(currentDate)
    }
}
