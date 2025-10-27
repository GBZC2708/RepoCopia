package com.example.alphakids.ui.screens.tutor.games

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.text.SimpleDateFormat
import java.util.*

data class CompletedWord(
    val wordId: String = "",
    val word: String = "",
    val imageUrl: String? = null,
    val audioUrl: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    fun getFormattedDate(): String {
        val formatter = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
        return formatter.format(Date(timestamp))
    }
}

object WordHistoryStorage {
    private const val PREFS_NAME = "word_history_prefs"
    private const val KEY_COMPLETED_WORDS = "completed_words"
    
    private val gson = Gson()
    
    private fun getPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }
    
    fun saveCompletedWord(
        context: Context,
        wordId: String,
        word: String,
        imageUrl: String?,
        audioUrl: String?
    ) {
        val completedWords = getCompletedWords(context).toMutableList()
        val newWord = CompletedWord(wordId, word, imageUrl, audioUrl)

        // Avoid duplicates by checking if the word was completed recently (within 5 minutes)
        val recentThreshold = System.currentTimeMillis() - (5 * 60 * 1000) // 5 minutes
        val isDuplicate = completedWords.any {
            it.wordId == wordId && it.timestamp > recentThreshold
        }

        if (!isDuplicate) {
            completedWords.add(newWord)
            saveCompletedWords(context, completedWords)
        }
    }
    
    fun getCompletedWords(context: Context): List<CompletedWord> {
        val prefs = getPreferences(context)
        val json = prefs.getString(KEY_COMPLETED_WORDS, null) ?: return emptyList()

        return try {
            val type = object : TypeToken<List<CompletedWord>>() {}.type
            gson.fromJson(json, type) ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    private fun saveCompletedWords(context: Context, words: List<CompletedWord>) {
        val prefs = getPreferences(context)
        val json = gson.toJson(words)
        prefs.edit().putString(KEY_COMPLETED_WORDS, json).apply()
    }
    
    fun clearHistory(context: Context) {
        val prefs = getPreferences(context)
        prefs.edit().remove(KEY_COMPLETED_WORDS).apply()
    }
}