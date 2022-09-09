package com.abhishek.mynotesnative

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.abhishek.mynotesnative.auth_activities.SignInActivity
import com.abhishek.mynotesnative.auth_fragments.SignInFragment
import com.abhishek.mynotesnative.auth_fragments.SignUpFragment
import com.abhishek.mynotesnative.databinding.ActivityLandingBinding
import com.abhishek.mynotesnative.notes.NotesGridActivity
import com.abhishek.mynotesnative.util.replaceFragment

class LandingActivity : AppCompatActivity(), RadioGroup.OnCheckedChangeListener {
    private lateinit var binding: ActivityLandingBinding
    private lateinit var encryptedSharedPrefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLandingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        initEncryptedPrefs()
        initListeners()
        verifySignIn()
    }

    override fun onCheckedChanged(group: RadioGroup, checkId: Int) {
        val checkRadioButton = group.findViewById<RadioButton>(group.checkedRadioButtonId)
        checkRadioButton?.let {
            when (checkRadioButton.id) {
                R.id.signin_rb -> replaceFragment(R.id.container, SignInFragment())
                else -> replaceFragment(R.id.container, SignUpFragment())
            }
        }
    }

    private fun initEncryptedPrefs() {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)
        encryptedSharedPrefs = EncryptedSharedPreferences.create(
            SignInActivity.FILENAME,
            mainKeyAlias,
            this,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
    }

    private fun initListeners() {
        val group = findViewById<RadioGroup>(R.id.radio_group)
        group.setOnCheckedChangeListener(this)
//        binding.signInBtn.setOnClickListener {
//            startActivity(
//                Intent(this, SignInActivity::class.java)
//            )
//        }
//        binding.signUpBtn.setOnClickListener {
//            startActivity(
//                Intent(this, SignUpActivity::class.java)
//            )
//        }
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, NotesGridActivity::class.java))
    }

    private fun verifySignIn() {
        if (encryptedSharedPrefs.contains(SignInActivity.EMAIL)
            && encryptedSharedPrefs.contains(
                SignInActivity.PASSWORD
            )
        ) {
            startHomeActivity()
        }
    }

    override fun onBackPressed() {
        val a = Intent(Intent.ACTION_MAIN).apply {
            addCategory(Intent.CATEGORY_HOME)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        startActivity(a)
    }
}