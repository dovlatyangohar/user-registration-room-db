package com.example.logindb

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "user_table")
data class User(
    var firstName: String,
    var lastName: String,
    var gender: String,
    @PrimaryKey
    var login: String,
    var email: String,
    var password: String,
    var imgPath: String
) : Parcelable