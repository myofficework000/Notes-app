package com.abhishek.mynotesnative.notes

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.abhishek.mynotesnative.R
import com.abhishek.mynotesnative.data.Note
import com.abhishek.mynotesnative.databinding.NoteBinding
import com.abhishek.mynotesnative.databinding.PasscodeDialogBinding
import com.abhishek.mynotesnative.db.PantryHelper

class NoteAdapter(private val context: Context, private val notes: List<Note>) :
    RecyclerView.Adapter<NoteAdapter.NoteViewHolder>() {

    private lateinit var binding: NoteBinding
    private lateinit var pantryHelper: PantryHelper
    private lateinit var passcodeDialogBinding: PasscodeDialogBinding

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        binding = NoteBinding.inflate(layoutInflater, parent, false)
        passcodeDialogBinding =
            PasscodeDialogBinding.inflate(layoutInflater, parent, false)
        pantryHelper = PantryHelper()
        return NoteViewHolder(binding.root)
    }

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NoteViewHolder, index: Int) {
        holder.apply {
            val note = notes[index]
            bind(note)
            itemView.setOnClickListener {
                if (note.isLocked == "true") {
                    showLockedNoteDialog(note, index)
                } else startNoteEditorActivity(note, index)
            }
        }
    }

    inner class NoteViewHolder(v: View) : RecyclerView.ViewHolder(v) {
        fun bind(note: Note) {
            binding.apply {
                noteTitle.text = note.title
                date.text = note.date
                if (note.isStarred == "true") {
                    starIcon.setImageResource(R.drawable.full_star_icon)
                }
                if (note.isLocked == "true") {
                    lockIcon.setImageResource(R.drawable.lock_icon)
                }
            }
        }
    }

    private fun showLockedNoteDialog(note: Note, index: Int) {
        val view = passcodeDialogBinding.root
        val builder = AlertDialog.Builder(context).apply {
            setView(view)
            setTitle("Passcode Required")
            setPositiveButton("View") { d, _ ->
                val dialogPasscode = passcodeDialogBinding.passcodeEt.text.toString()
                if (dialogPasscode != note.passcode) {
                    showIncorrectCodeMsg()
                } else {
                    startNoteEditorActivity(note, index)
                }
                passcodeDialogBinding.passcodeEt.text?.clear()
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

    private fun showIncorrectCodeMsg() {
        Snackbar.make(
            context,
            binding.root,
            "Incorrect passcode",
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun startNoteEditorActivity(note: Note, index: Int) {
        val intent =
            Intent(context, NoteEditorActivity::class.java)
        intent.putExtra(NOTE_DATA, note)
        intent.putExtra("mode", "update")
        intent.putExtra("index", index)
        context.startActivity(intent)
    }

    companion object {
        const val NOTE_DATA = "note"
    }
}