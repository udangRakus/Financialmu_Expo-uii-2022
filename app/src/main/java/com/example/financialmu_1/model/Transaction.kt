package com.example.financialmu_1.model

import com.google.firebase.Timestamp

data class Transaction(
    var id: String?,
    var username: String,
    var category: String,
    var amount: Int,
    var type: String,
    var note: String,
    var created: Timestamp? = Timestamp.now()
)
