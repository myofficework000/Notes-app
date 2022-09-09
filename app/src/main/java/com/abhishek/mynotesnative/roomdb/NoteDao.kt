package com.abhishek.mynotesnative.roomdb

import androidx.room.*
import com.abhishek.mynotesnative.data.Note

@Dao
interface NoteDao {
    @Insert
    fun insert(note: Note)

    @Delete
    fun delete(note: Note)

    @Update
    fun update(note: Note)

    @Query("SELECT * FROM Note")
    fun getAllNotes(): MutableList<Note>
}