package com.abhishek.mynotesnative.auth_activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import com.abhishek.mynotesnative.databinding.ActivitySignInBinding
import com.abhishek.mynotesnative.db.DatabaseHelper
import com.abhishek.mynotesnative.notes.NotesGridActivity

class SignInActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignInBinding
    private lateinit var encryptedSharedPrefs: SharedPreferences
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        databaseHelper = DatabaseHelper(applicationContext)
        setContentView(binding.root)
        supportActionBar?.hide()
        initEncryptedPrefs()
//        initView()
    }

    private fun initEncryptedPrefs() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        encryptedSharedPrefs = EncryptedSharedPreferences.create(
            FILENAME,
            mainKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

//    private fun initView() {
//        binding.apply {
//            binding.signInBTN.setOnClickListener {
//                verifySignIn()
//            }
//        }
//    }

    private fun startHomeActivity() {
        startActivity(Intent(this, NotesGridActivity::class.java))
    }

    private fun showSnackbar(str: String) {
        Snackbar.make(
            this,
            binding.root,
            str,
            Snackbar.LENGTH_SHORT
        ).show()
    }

//    private fun verifySignIn() {
//        if (binding.emailEtSignIn.text.isNotEmpty() && binding.passwordEtSignIn.text.isNotEmpty()) {
//            if (encryptedSharedPrefs.contains(EMAIL) && encryptedSharedPrefs.contains(PASSWORD)) {
//                startHomeActivity()
//            } else {
//                showSnackbar("Account not found. Please create an account")
//            }
//        } else {
//            showSnackbar("Please ensure both fields aren't empty")
//        }
//    }

    companion object {
        const val FILENAME = "login-details"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }
}