package com.example.buynow.presentation.activity

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.buynow.R
import com.example.buynow.data.model.User
import com.example.buynow.utils.Extensions.toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class SignUpActivity : AppCompatActivity() {

    private lateinit var fullName: EditText
    private lateinit var emailEt: EditText
    private lateinit var passEt: EditText
    private lateinit var CpassEt: EditText
    private lateinit var progressDialog: ProgressDialog

    private val userCollectionRef = Firebase.firestore.collection("Users")
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    private val emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        val signUpBtn = findViewById<Button>(R.id.signUpBtn_signUpPage)
        fullName = findViewById(R.id.nameEt_signUpPage)
        emailEt = findViewById(R.id.emailEt_signUpPage)
        passEt = findViewById(R.id.PassEt_signUpPage)
        CpassEt = findViewById(R.id.cPassEt_signUpPage)
        val signInTv = findViewById<TextView>(R.id.signInTv_signUpPage)

        progressDialog = ProgressDialog(this)

        textAutoCheck()

        signInTv.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        signUpBtn.setOnClickListener {
            checkInput()
        }
    }

    private fun textAutoCheck() {
        fullName.addValidationCheck { it.length >= 4 }
        emailEt.addValidationCheck { it.matches(emailPattern.toRegex()) }
        passEt.addValidationCheck { it.length > 5 }
        CpassEt.addValidationCheck { it == passEt.text.toString() }
    }

    private fun EditText.addValidationCheck(condition: (String) -> Boolean) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                updateDrawable(condition(s.toString()))
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                updateDrawable(false)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                updateDrawable(condition(s.toString()))
            }

            private fun updateDrawable(isValid: Boolean) {
                val checkIcon = if (isValid) ContextCompat.getDrawable(applicationContext, R.drawable.ic_check) else null
                setCompoundDrawablesWithIntrinsicBounds(null, null, checkIcon, null)
            }
        })
    }

    private fun checkInput() {
        val name = fullName.text.toString().trim()
        val email = emailEt.text.toString().trim()
        val pass = passEt.text.toString().trim()
        val cpass = CpassEt.text.toString().trim()

        when {
            name.isEmpty() -> toast("Name can't be empty!")
            email.isEmpty() -> toast("Email can't be empty!")
            !email.matches(emailPattern.toRegex()) -> toast("Enter a valid email")
            pass.isEmpty() -> toast("Password can't be empty!")
            pass != cpass -> toast("Passwords do not match")
            else -> registerUser(name, email, pass)
        }
    }

    private fun registerUser(name: String, email: String, password: String) {
        progressDialog.setTitle("Please Wait")
        progressDialog.setMessage("Creating Account")
        progressDialog.show()

        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val uid = firebaseAuth.uid ?: return@addOnCompleteListener
                    val user = User(
                        userName = name,
                        userPhone = "",
                        userUid = uid,
                        userEmail = email,
                        userImage = "",
                        userAddress = ""
                    )
                    saveUserToFirestore(user)
                } else {
                    progressDialog.dismiss()
                    toast("Signup failed: ${task.exception?.message}")
                }
            }
    }

    private fun saveUserToFirestore(user: User) = CoroutineScope(Dispatchers.IO).launch {
        try {
            userCollectionRef.document(user.userUid).set(user).await()
            withContext(Dispatchers.Main) {
                toast("User account created")
                progressDialog.dismiss()
                val intent = Intent(this@SignUpActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                toast("Error: ${e.message}")
                progressDialog.dismiss()
            }
        }
    }
}
