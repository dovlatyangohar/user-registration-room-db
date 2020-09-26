package com.example.logindb

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_user_page.*

class UserPageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_page)

        val receivedUserIntent = intent
        val user = receivedUserIntent.getParcelableExtra<User>("user")

        if (user != null) {
            firstNameTextViewDetailed.text = user.firstName
            lastNameTextViewDetailed.text = user.lastName
            loginDetailed.text = user.login
            genderTextViewDetailed.text = user.gender
            emailDetailed.text = user.email
            coverImgDetailed.setImageBitmap(BitmapFactory.decodeFile(user.imgPath))
        }
    }


}