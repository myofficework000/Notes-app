package com.abhishek.mynotesnative.auth_activities

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.snackbar.Snackbar
import com.abhishek.mynotesnative.databinding.ActivitySignUpBinding
import com.abhishek.mynotesnative.db.DatabaseHelper
import com.abhishek.mynotesnative.notes.NotesGridActivity

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var encryptedSharedPrefs: SharedPreferences
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        databaseHelper = DatabaseHelper(applicationContext)
        setContentView(binding.root)
//        binding.animationView.setMinAndMaxFrame(67, 120)
//        supportActionBar?.hide()
//        initEncryptedPrefs()
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
        editor = encryptedSharedPrefs.edit()
    }

//    private fun initView() {
//        binding.apply {
//            binding.signUpBtn.setOnClickListener {
//                if (emailEt.text.isNotEmpty()
//                    || passwordEt.text.isNotEmpty()
//                    || cPasswordEt.text.isNotEmpty()
//                ) {
//                    if (passwordEt.text.toString() == cPasswordEt.text.toString()) {
//                        signUp(emailEt.text.toString(), passwordEt.text.toString())
//                    } else showSnackbar("Passwords do not match")
//                } else showSnackbar("Please ensure all fields aren't empty")
//            }
//        }
//    }

    private fun showSnackbar(str: String) {
        Snackbar.make(
            this@SignUpActivity,
            binding.root,
            str,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun signUp(email: String, password: String) {
        editor.apply {
            putString(EMAIL, email)
            putString(PASSWORD, password)
            if (commit()) {
                startHomeActivity()
                finish()
            }
        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, NotesGridActivity::class.java))
    }

    companion object {
        const val FILENAME = "login-details"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }
}