package com.example.logindb

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.room.Room
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_register.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {
    lateinit var login: String
    var userIsLogged: Boolean = false


    private val db by lazy {
        Room.databaseBuilder(
            applicationContext,
            UserDatabase::class.java, "users"
        ).build()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        registerButton.setOnClickListener {
            sendIntent()
        }

        loginButton.setOnClickListener {
            login()
        }
        refreshUsers()

    }

    val REQUEST_CODE = 1

    private fun sendIntent() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivityForResult(intent, REQUEST_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (data != null) {
                    val user: User? = data.getParcelableExtra("user")
                    if (user != null) {
                        login = user.login
                        GlobalScope.launch(Dispatchers.IO) {

                            val loggedUser: User? = db.userDao().loadLoggedUser(login)
                            userIsLogged = loggedUser != null

                            Log.i("userIsLogged", userIsLogged.toString())
                            if (!userIsLogged) {
                                db.userDao().insertUser(user)
                                refreshUsers()
                            } else withContext(Dispatchers.Main) {

                                Toast.makeText(
                                    this@MainActivity,
                                    "Login is existing in db,pleas log in",
                                    Toast.LENGTH_LONG
                                ).show()
                                loginEditText.setText(login)
                                //  loginFromRegister(user) // don't think this is a good idea


                            }
                        }
                    }
                }
            }
        }
    }

    private fun login() {
        GlobalScope.launch(Dispatchers.IO) {
            val currentUser = db.userDao().loadLoggedUser(loginEditText.text.toString())
            if (currentUser?.login != null) {
                val userPassword =
                    currentUser.password
                if (passwordEditText.text.toString() == userPassword) {

                    withContext(Dispatchers.Main) {
                        Log.i("LOGIN", "pass true")//open activity

                        openUserPage(currentUser)
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.i("LOGIN", "pass false")
                }
            }
        }
    }

    private fun loginFromRegister(user: User) {
        GlobalScope.launch(Dispatchers.IO) {
            val currentUser = db.userDao().loadLoggedUser(user.login)
            if (currentUser?.login != null) {
                if (user.password == currentUser.password) {
                    withContext(Dispatchers.Main) {
                        Log.i("REG_LOGIN", "pass true")//open activity

                        openUserPage(currentUser)
                    }
                } else {
                    withContext(Dispatchers.Main) {
                        Log.i("LOGIN", "pass false")
                    }
                }
            } else withContext(Dispatchers.Main) {
                Toast.makeText(this@MainActivity, "null", Toast.LENGTH_LONG).show()
            }
        }

    }

    private fun openUserPage(user: User) {
        val openUserPageIntent = Intent(this, UserPageActivity::class.java).apply {
            putExtra("user", user)
        }
        startActivity(openUserPageIntent)

    }

    private fun refreshUsers() {
        GlobalScope.launch(Dispatchers.IO) {
            val text = StringBuilder("Users:\n")
            db.userDao().getAllUsers().forEach {
                text.append("$it\n")
            }
            withContext(Dispatchers.Main) {
                usersTextView.text = text
            }
        }
    }

}

