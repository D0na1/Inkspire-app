package com.example.inkspire_app.data

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class JournalViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: JournalRepository
    val allEntries: LiveData<List<JournalEntry>>

    init {
        val journalEntryDao = JournalDatabase.getDatabase(application).journalEntryDAO()
        repository = JournalRepository(journalEntryDao)
        allEntries = repository.allEntries
    }

    fun insert(entry: JournalEntry) = viewModelScope.launch {
        repository.insert(entry)
    }

    fun update(entry: JournalEntry) = viewModelScope.launch {
        repository.update(entry)
    }

    fun getEntryById(entryId: Int): LiveData<JournalEntry> {
        return repository.getEntryById(entryId)
    }

    fun delete(entry: JournalEntry) = viewModelScope.launch {
        repository.delete(entry)
    }
}
