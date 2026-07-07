package com.example.Roomdb.domain.utils

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateFormatter {

    // Parses the raw sentAt string from the API
    // Format from API: "2026-06-23T07:02:34.381037"
    private fun parse(sentAt: String): Date? {
        val normalized = normalizeFractionalSeconds(sentAt)
        return try {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.getDefault()).parse(normalized)
        } catch (e: Exception) {
            try {
                SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()).parse(sentAt)
            } catch (e2: Exception) {
                null
            }
        }
    }

    private fun normalizeFractionalSeconds(sentAt: String): String {
        val dotIndex = sentAt.indexOf('.')
        if (dotIndex == -1) return sentAt // no fractional part present
        val wholePart = sentAt.substring(0, dotIndex)
        val fraction = sentAt.substring(dotIndex + 1).takeWhile { it.isDigit() }
        val millis = fraction.padEnd(3, '0').take(3)
        return "$wholePart.$millis"
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

    fun toListTime(sentAt: String): String {
        val date = parse(sentAt) ?: return sentAt.take(10)
        return when {
            isToday(date) -> SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
            isYesterday(date) -> "Yesterday"
            else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(date)
        }
    }

    fun toBubbleTime(sentAt: String): String {
        val date = parse(sentAt) ?: return sentAt.take(5)
        return SimpleDateFormat("h:mm a", Locale.getDefault()).format(date)
    }
}