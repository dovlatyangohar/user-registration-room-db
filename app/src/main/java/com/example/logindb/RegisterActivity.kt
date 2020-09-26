package com.example.logindb

import android.content.Intent
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Patterns
import android.widget.EditText
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_register.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class RegisterActivity : AppCompatActivity() {
    lateinit var user: User

    private lateinit var currentPath: String
    private lateinit var returnCurrentPath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        addPhotoButton.setOnClickListener {
            dispatchTakePictureIntent()
        }

        saveButton.isEnabled = false
        val watcher: TextWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                enableSaveButton()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

            }

            override fun afterTextChanged(s: Editable?) {}


        }

        firstNameEditText.addTextChangedListener(watcher)
        lastNameEditText.addTextChangedListener(watcher)

        femaleRadioButton.addTextChangedListener(watcher)
        maleRadioButton.addTextChangedListener(watcher)

        chooseLoginEditText.addTextChangedListener(watcher)
        emailEditText.addTextChangedListener(watcher)
        choosePasswordEditText.addTextChangedListener(watcher)



        saveButton.setOnClickListener {
            if (photoAddedCheckbox.isChecked && isValid()) {
                saveButton.isEnabled = true
            }
            val firstName = firstNameEditText.text.toString()
            val lastName = lastNameEditText.text.toString()
            val login = chooseLoginEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = choosePasswordEditText.text.toString()
            val childCount = radioGroup.childCount
            var gender = ""

            for (i in 0 until childCount) {
                val button: RadioButton = radioGroup.getChildAt(i) as RadioButton

                when (button.id) {
                    R.id.femaleRadioButton -> button.text = "Female"
                    R.id.maleRadioButton -> button.text = "Male"
                    else -> "Other"
                }
                if (button.isChecked) {
                    gender = button.text.toString()
                }
            }

            user = User(firstName, lastName, gender, login, email, password, returnCurrentPath)

            returnIntent(user)
//            finish()
        }

    }

    private fun returnIntent(user: User) {
        val returnIntent = Intent()
        returnIntent.putExtra("user", user)
        setResult(RESULT_OK, returnIntent)
        finish()
    }

    val REQUEST_TAKE_PHOTO = 1

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null) {
            var photoFile: File? = null
            try {
                photoFile = createImageFile()
            } catch (e: IOException) {
                e.printStackTrace()
            }
            if (photoFile != null) {
                val photoUri = FileProvider.getUriForFile(this, "com.example.logindb", photoFile)
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
                startActivityForResult(
                    takePictureIntent,
                    REQUEST_TAKE_PHOTO
                )
            }
        }
    }

    val REQUEST_IMAGE_CAPTURE = 1

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            photoAddedCheckbox.isChecked = true
            enableSaveButton()
            returnCurrentPath = currentPath

        }
    }


    private fun createImageFile(): File? {
        val timeStamp = SimpleDateFormat("yyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(imageFileName, ".jpg", storageDir)
        currentPath = image.absolutePath
        return image
    }


    private fun isEmpty(text: EditText): Boolean {
        val str = text.text.toString()
        return TextUtils.isEmpty(str)
    }

    private fun isEmail(text: EditText): Boolean {
        val email = text.text.toString()
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun isValid(): Boolean {
        if (isEmpty(firstNameEditText)) {
            firstNameEditText.error = "Username is required"
            return false
        }
        if (!isEmail(emailEditText)) {
            emailEditText.error = "Email isn't valid"
            return false
        }
        if (!(femaleRadioButton.isChecked || maleRadioButton.isChecked)) {
            genderTextView.error = "Select gender"
            return false
        }
        if (isEmpty(choosePasswordEditText)) {
            choosePasswordEditText.error = "Password is required"
            return false
        }
        return true
    }

    private fun enableSaveButton() {
        saveButton.isEnabled = photoAddedCheckbox.isChecked && isValid()
    }
}