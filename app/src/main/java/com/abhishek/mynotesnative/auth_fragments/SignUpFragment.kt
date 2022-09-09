package com.abhishek.mynotesnative.auth_fragments

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.android.volley.Request
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.material.snackbar.Snackbar
import com.abhishek.mynotesnative.R
import com.abhishek.mynotesnative.constant.Constant.BASE_URL
import com.abhishek.mynotesnative.constant.Constant.SIGN_UP_END_POINT
import com.abhishek.mynotesnative.db.DatabaseHelper
import com.abhishek.mynotesnative.notes.NotesGridActivity
import org.json.JSONObject


class SignUpFragment : Fragment() {
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var encryptedSharedPrefs: SharedPreferences
    private lateinit var databaseHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_sign_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        databaseHelper = DatabaseHelper(view.context)
        initEncryptedPrefs(view.context)
        initView(view)
    }

    private fun initEncryptedPrefs(context: Context) {
        val keyGenParameterSpec = MasterKeys.AES256_GCM_SPEC
        val mainKeyAlias = MasterKeys.getOrCreate(keyGenParameterSpec)

        encryptedSharedPrefs = EncryptedSharedPreferences.create(
            FILENAME,
            mainKeyAlias,
            context,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )
        editor = encryptedSharedPrefs.edit()
    }

    private fun initView(view: View) {
        val signUpBtn = view.findViewById<Button>(R.id.sign_up_btn)
        val emailEt = view.findViewById<EditText>(R.id.email_et)
        val passwordEt = view.findViewById<EditText>(R.id.password_et)
        val cPasswordEt = view.findViewById<EditText>(R.id.cPassword_et)
        signUpBtn.setOnClickListener {
            if (emailEt.text.isNotEmpty()
                || passwordEt.text.isNotEmpty()
                || cPasswordEt.text.isNotEmpty()
            ) {
                if (passwordEt.text.toString() == cPasswordEt.text.toString()) {
                    val data = JSONObject().apply {
                        // TODO : Create fields for name and number
                        put("full_name", "Brian Jr")
                        put("mobile_no", 2122234566)
                        put("email_id", emailEt.text.toString())
                        put("password", passwordEt.text.toString())
                    }
                    makeSignUpApiRequest(view, data)
                    signUp(emailEt.text.toString(), passwordEt.text.toString(), view.context)
                } else showSnackbar("Passwords do not match", view)
            } else showSnackbar("Please ensure all fields aren't empty", view)
        }
    }

    private fun makeSignUpApiRequest(view: View, data: JSONObject) {
        val cpb = view.findViewById<ProgressBar>(R.id.progress_bar_signUp)
        cpb.visibility = View.VISIBLE
        val requestQueue = Volley.newRequestQueue(view.context)
        val url = BASE_URL + SIGN_UP_END_POINT
        val request = JsonObjectRequest(Request.Method.POST, url, data, { response: JSONObject ->
            val msg = response.getString("message")
            Log.i("msg", msg)
            cpb.visibility = View.GONE
            startHomeActivity(view.context)
        }, { error: VolleyError -> error.printStackTrace() })
        requestQueue.add(request)
    }

    private fun showSnackbar(str: String, view: View) {
        Snackbar.make(
            view.context,
            view.findViewById(R.id.sign_up_root),
            str,
            Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun signUp(email: String, password: String, context: Context) {
        editor.apply {
            putString(EMAIL, email)
            putString(PASSWORD, password)
            if (commit()) {
                startHomeActivity(context)
            }
        }
    }

    private fun startHomeActivity(context: Context) {
        startActivity(Intent(context, NotesGridActivity::class.java))
    }

    companion object {
        const val FILENAME = "login-details"
        const val EMAIL = "email"
        const val PASSWORD = "password"
    }
}