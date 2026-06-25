package com.example.Roomdb.domain.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {

    // Parses the raw sentAt string from the API
    // Format from API: "2026-06-23T07:02:34.381037"
    private fun parse(sentAt: String): Date? {
        val formats = listOf(
            "yyyy-MM-dd'T'HH:mm:ss.SSSSSS",
            "yyyy-MM-dd'T'HH:mm:ss.SSS",
            "yyyy-MM-dd'T'HH:mm:ss"
        )
        for (format in formats) {
            try {
                return SimpleDateFormat(format, Locale.getDefault()).parse(sentAt)
            } catch (e: Exception) {
                continue
            }
        }
        return null
    }

    private fun isToday(date: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date }
        val cal2 = Calendar.getInstance()
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    private fun isYesterday(date: Date): Boolean {
        val cal1 = Calendar.getInstance().apply { time = date }
        val cal2 = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR)
    }

    // Used in ChatListTab — shows "h:mm a" / "Yesterday" / "MMM d"
    fun toListTime(sentAt: String): String {
        val date = parse(sentAt) ?: return sentAt.take(10)
        return when {
            isToday(date) -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            isYesterday(date) -> "Yesterday"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
    }

    // Used in ChatScreen MessageBubble — shows "h:mm a" only
    fun toBubbleTime(sentAt: String): String {
        val date = parse(sentAt) ?: return sentAt.take(5)
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    }
}