package com.example.inkspire_app.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface JournalEntryDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entry: JournalEntry)

    @Update
    suspend fun update(entry: JournalEntry)

    @Query("SELECT * FROM journal_entries WHERE id = :entryId")
    fun getEntryById(entryId: Int): LiveData<JournalEntry>

    @Query("SELECT * FROM journal_entries")
    fun getAllEntries(): LiveData<List<JournalEntry>>

    @Delete
    suspend fun delete(entry: JournalEntry)
}
