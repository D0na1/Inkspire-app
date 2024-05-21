package com.example.inkspire_app.data

import androidx.lifecycle.LiveData

class JournalRepository(private val journalEntryDao: JournalEntryDAO) {
    val allEntries: LiveData<List<JournalEntry>> = journalEntryDao.getAllEntries()

    suspend fun insert(entry: JournalEntry) {
        journalEntryDao.insert(entry)
    }

    suspend fun update(entry: JournalEntry) {
        journalEntryDao.update(entry)
    }

    fun getEntryById(entryId: Int): LiveData<JournalEntry> {
        return journalEntryDao.getEntryById(entryId)
    }

    suspend fun delete(entry: JournalEntry) {
        journalEntryDao.delete(entry)
    }
}
