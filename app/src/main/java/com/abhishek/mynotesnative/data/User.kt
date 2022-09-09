package com.abhishek.mynotesnative.data

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class User(
    var email_id: String,
    var full_name: String,
    var mobile_no: String,
    var user_id: String
) : Parcelable