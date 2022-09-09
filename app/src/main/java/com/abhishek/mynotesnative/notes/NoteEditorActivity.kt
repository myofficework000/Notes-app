package com.abhishek.mynotesnative.notes

import android.content.Intent
import android.os.Bundle
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.abhishek.mynotesnative.R
import com.abhishek.mynotesnative.data.Note
import com.abhishek.mynotesnative.databinding.ActivityNoteEditorBinding
import com.abhishek.mynotesnative.databinding.ActivityNotesGridBinding
import com.abhishek.mynotesnative.databinding.PasscodeDialogBinding
import com.abhishek.mynotesnative.db.DatabaseHelper
import com.abhishek.mynotesnative.db.PantryHelper
import com.abhishek.mynotesnative.notes.NoteAdapter.Companion.NOTE_DATA
import com.abhishek.mynotesnative.roomdb.AppDatabase
import com.abhishek.mynotesnative.roomdb.NoteDao
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class NoteEditorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoteEditorBinding
    private lateinit var gridBinding: ActivityNotesGridBinding
    private lateinit var passcodeDialogBinding: PasscodeDialogBinding
    private lateinit var databaseHelper: DatabaseHelper
    private lateinit var pantryHelper: PantryHelper
    private var noteDao: NoteDao? = null

    private val current = LocalDateTime.now()
    private val formatter = DateTimeFormatter.ofPattern("M/d/yyyy")
    private val now = current.format(formatter)

    private var mode = "save"
    private var index = 0
    private var isStarred = false
    private var isLocked = false
    private var textColor = "Blueish_idk"
    private var bodyTextSize = "14"
    private var passcode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNoteEditorBinding.inflate(layoutInflater)
        gridBinding = ActivityNotesGridBinding.inflate(layoutInflater)
        passcodeDialogBinding = PasscodeDialogBinding.inflate(layoutInflater)
        databaseHelper = DatabaseHelper(this)
        pantryHelper = PantryHelper()
        setContentView(binding.root)
        supportActionBar?.hide()
        val appDB: AppDatabase? = application.let {
            AppDatabase.getInstance(applicationContext)
        }
        noteDao = appDB?.getBlogDao()
        initView()
        initListeners()
    }

    private fun initView() {
        if (intent.extras != null) {
            val note = intent.extras?.get(NOTE_DATA) as Note?
            if (note != null) {
                textColor = note.textColor
                passcode = note.passcode
                bodyTextSize = note.bodyFontSize
                index = note.index
                mode = intent.extras?.getString("mode") ?: mode
                binding.apply {
                    titleEt.setText(note.title)
                    titleEt.setTextColor(getTextColor(note.textColor))
                    bodyET.setText(note.body)
                    bodyET.setTextColor(getTextColor(note.textColor))
                    bodyET.textSize = note.bodyFontSize.toFloat()
                    dateModified.text = note.date
                    colorLens.setColorFilter(getTextColor(note.textColor))
                    when (note.isStarred) {
                        "true" -> {
                            starIcon.setImageResource(R.drawable.starred_36)
                            isStarred = true
                        }
                        "false" -> {
                            starIcon.setImageResource(R.drawable.star_icon_36)
                            isStarred = false
                        }
                    }
                    when (note.isLocked) {
                        "true" -> {
                            lockIcon.setImageResource(R.drawable.locked_36)
                            isLocked = true
                        }
                        "false" -> {
                            lockIcon.setImageResource(R.drawable.unlock_36)
                            isLocked = false
                        }
                    }
                }
            }
        }
    }

    private fun initListeners() {
        binding.abcIcon.setOnClickListener {
            toggleTextSize()
        }
        binding.colorLens.setOnClickListener {
            showColorOptions()
        }
        binding.clearIcon.setOnClickListener {
            clear()
        }
        binding.lockIcon.setOnClickListener {
            when (isLocked) {
                true -> clearNoteLockDialog()
                false -> showLockedNoteDialog()
            }
        }
        binding.saveIcon.setOnClickListener {
            saveNote(
                Note(
                    title = binding.titleEt.text.toString(),
                    body = binding.bodyET.text.toString(),
                    date = now,
                    passcode = passcode,
                    bodyFontSize = bodyTextSize,
                    textColor = textColor,
                    isStarred = isStarred.toString(),
                    isLocked = isLocked.toString(),
                    index = index
                )
            )
        }
        binding.starIcon.setOnClickListener {
            isStarred = !isStarred
            when (isStarred) {
                true -> binding.starIcon.setImageResource(R.drawable.starred_36)
                false -> binding.starIcon.setImageResource(R.drawable.star_icon_36)
            }
        }
    }

    private fun clear() {
        val title = binding.titleEt.text
        val body = binding.bodyET.text
        if (title.isNotEmpty() || body.isNotEmpty()) {
            showConfirmClearDialog()
        }
    }

    private fun clearNoteLockDialog() {
        val view = passcodeDialogBinding.root
        val builder = AlertDialog.Builder(this).apply {
            setTitle("Unlock Note and clear passcode?")
            setPositiveButton("Unlock") { d, _ ->
                passcode = ""
                isLocked = false
                binding.lockIcon.setImageResource(R.drawable.unlock_36)
                showSnackbar("Note unlocked. Save the Note to confirm")
                d.dismiss()
            }
            setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
            }
        }
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        builder.show()
    }

    private fun getTextColor(str: String): Int {
        return when (str) {
            "Blueish_idk" -> getColor(R.color.blueish_idk)
            "Pink" -> getColor(R.color.pink)
            "Green" -> getColor(R.color.green)
            "Red" -> getColor(R.color.red)
            else -> getColor(R.color.white)
        }
    }

    private fun toggleTextSize() {
        when (bodyTextSize.toFloat()) {
            in 14.0f..27.0f -> {
                binding.bodyET.textSize = 28.0f
                bodyTextSize = "28"
            }
            in 28.0f..41.0f -> {
                binding.bodyET.textSize = 42.0f
                bodyTextSize = "42"
            }
            in 42.0f..42.0f -> {
                binding.bodyET.textSize = 14.0f
                bodyTextSize = "14"
            }
        }
    }

    private fun saveNote(note: Note) {
        binding.apply {
            val title = titleEt.text.toString()
            val body = bodyET.text.toString()
            if (title.isNotEmpty() && body.isNotEmpty()) {
                if (passcode.isEmpty()) isLocked = false
                when (mode) {
                    "update" -> {
                        noteDao?.update(note)
//                        databaseHelper.updateNote(note)
//                        pantryHelper.updateNote(note, binding.root)
                    }
                    "save" -> {
                        noteDao?.insert(note)
////                        databaseHelper.addNote(note)
//                        pantryHelper.saveNote(note, binding.root)
                    }
                }
                startNoteGridActivity()
            } else showSnackbar("Please ensure both fields aren't empty")
        }
    }

    private fun showColorOptions() {
        val options = arrayOf("Blueish_idk", "Pink", "Green", "Red", "White")
        val builder = AlertDialog.Builder(this)
            .setTitle("Choose Text Color")
            .setSingleChoiceItems(options, -1) { d, pos ->
                when (options[pos]) {
                    "Blueish_idk" -> {
                        textColor = options[0]
                        setEditTextColor(binding, color = textColor)
                    }
                    "Pink" -> {
                        textColor = options[1]
                        setEditTextColor(binding, color = textColor)
                    }
                    "Green" -> {
                        textColor = options[2]
                        setEditTextColor(binding, color = textColor)
                    }
                    "Red" -> {
                        textColor = options[3]
                        setEditTextColor(binding, color = textColor)
                    }
                    "White" -> {
                        textColor = options[4]
                        setEditTextColor(binding, color = textColor)
                    }
                }
                d.dismiss()
            }
        builder.create().show()
    }

    private fun showConfirmClearDialog() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Clear Fields")
            .setMessage("This will clear the title and body fields")
            .setPositiveButton("Clear") { _, _ ->
                binding.titleEt.text.clear()
                binding.bodyET.text.clear()
                Snackbar.make(binding.root, "Fields cleared", Snackbar.LENGTH_SHORT).show()
            }
        builder.create().show()
    }

    private fun showLockedNoteDialog() {
        val view = passcodeDialogBinding.root
        val builder = AlertDialog.Builder(this).apply {
            setView(view)
            setTitle("Enter passcode to lock Note")
            setCancelable(false)
            setPositiveButton("Lock") { d, _ ->
                val dialogPasscode = passcodeDialogBinding.passcodeEt.text.toString()
                if (dialogPasscode.isNotEmpty()) {
                    passcode = dialogPasscode
                    isLocked = true
                    binding.lockIcon.setImageResource(R.drawable.locked_36)
                    showSnackbar("Note locked. Save the Note to confirm")
                    d.dismiss()
                } else showSnackbar("Please provide a passcode")
            }
            setNegativeButton("Cancel") { d, _ ->
                d.dismiss()
            }
        }
        if (view.parent != null) {
            (view.parent as ViewGroup).removeView(view)
        }
        builder.show()
    }

    private fun showSnackbar(str: String) {
        Snackbar.make(
            this@NoteEditorActivity,
            binding.root,
            str,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun setEditTextColor(binding: ActivityNoteEditorBinding, color: String) {
        binding.titleEt.setTextColor(getTextColor(color))
        binding.bodyET.setTextColor(getTextColor(color))
        binding.colorLens.setColorFilter(getTextColor(color))
    }

    private fun startNoteGridActivity() {
        this.startActivity(
            Intent(
                this@NoteEditorActivity,
                NotesGridActivity::class.java
            )
        )
        finish()
    }
}