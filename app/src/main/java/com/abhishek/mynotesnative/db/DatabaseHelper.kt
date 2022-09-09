package com.abhishek.mynotesnative.db

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.abhishek.mynotesnative.data.Note

class DatabaseHelper(context: Context) : SQLiteOpenHelper(
    context, DATABASE_NAME, null, DATABASE_VERSION
) {

    override fun onCreate(db: SQLiteDatabase?) {
        val query =
            ("CREATE TABLE $TABLE_NAME (" +
                    "$ID INTEGER PRIMARY KEY, " +
                    "$TITLE TEXT, " +
                    "$BODY TEXT, " +
                    "$DATE TEXT, " +
                    "$PASSCODE TEXT, " +
                    "$BODY_FONT_SIZE TEXT, " +
                    "$TEXT_COLOR TEXT, " +
                    "$IS_STARRED TEXT, " +
                    "$IS_LOCKED TEXT)"
                    )
        db?.execSQL(query)
    }

    override fun onUpgrade(db: SQLiteDatabase?, p1: Int, p2: Int) {
        db?.execSQL(("DROP TABLE IF EXISTS $TABLE_NAME"))
        onCreate(db)
    }

    fun addNote(note: Note) {
        val contentValues = ContentValues().apply {
            put(TITLE, note.title)
            put(BODY, note.body)
            put(DATE, note.date)
            put(PASSCODE, note.passcode)
            put(BODY_FONT_SIZE, note.bodyFontSize)
            put(TEXT_COLOR, note.textColor)
            put(IS_STARRED, note.isStarred)
            put(IS_LOCKED, note.isLocked)
        }
        writableDatabase.apply {
            insert(TABLE_NAME, null, contentValues)
        }
    }

    fun deleteNote(note: Note) {
        writableDatabase.apply {
            delete(TABLE_NAME, "$ID = ${note.index + 1}", null)
        }
    }

    fun updateNote(note: Note) {
        val contentValues = ContentValues().apply {
            put(TITLE, note.title)
            put(BODY, note.body)
            put(DATE, note.date)
            put(PASSCODE, note.passcode)
            put(BODY_FONT_SIZE, note.bodyFontSize)
            put(TEXT_COLOR, note.textColor)
            put(IS_STARRED, note.isStarred)
            put(IS_LOCKED, note.isLocked)
        }
        writableDatabase.apply {
            update(TABLE_NAME, contentValues, "$ID = ${note.index + 1}", null)
        }
    }

    fun getNotes(): Cursor? {
        return readableDatabase.rawQuery(("SELECT * FROM $TABLE_NAME"), null)
    }

    companion object {
        const val DATABASE_NAME = "notes-db"
        const val DATABASE_VERSION = 1
        const val TABLE_NAME = "notes"
        const val TITLE = "title"
        const val BODY = "body"
        const val DATE = "date"
        const val PASSCODE = "passcode"
        const val BODY_FONT_SIZE = "bFontSize"
        const val TEXT_COLOR = "textColor"
        const val IS_STARRED = "isStarred"
        const val IS_LOCKED = "isLocked"
        const val ID = "id"
    }
}