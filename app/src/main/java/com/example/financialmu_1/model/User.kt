package com.example.financialmu_1.model

import com.google.firebase.Timestamp

data class User(
    var email: String,
    var username: String,
    var password: String,
    var created: Timestamp
)
