package com.example.buynow.presentation.activity

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.buynow.R
import com.example.buynow.utils.Extensions.toast
import com.example.buynow.utils.FirebaseUtils.firebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var signInBtn: Button
    private lateinit var emailEt: EditText
    private lateinit var passEt: EditText
    private lateinit var emailError: TextView
    private lateinit var passwordError: TextView
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var signUpTv: TextView
    private lateinit var forgottenPassTv: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        signInBtn = findViewById(R.id.loginBtn)
        emailEt = findViewById(R.id.emailEt)
        passEt = findViewById(R.id.PassEt)
        emailError = findViewById(R.id.emailError)
        passwordError = findViewById(R.id.passwordError)
        signUpTv = findViewById(R.id.signUpTv)
        forgottenPassTv = findViewById(R.id.forgottenPassTv)

        loadingDialog = LoadingDialog(this)

        textAutoCheck()

        signInBtn.setOnClickListener {
            checkInput()
        }

        signUpTv.setOnClickListener {
            startActivity(Intent(this, SignUpActivity::class.java))
        }
forgottenPassTv.setOnClickListener {
            startActivity(Intent(this, ForgotPassword::class.java))
        }
    }

    private fun textAutoCheck() {
        emailEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (emailEt.text.isEmpty()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (Patterns.EMAIL_ADDRESS.matcher(emailEt.text).matches()) {
                    emailEt.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                    emailError.visibility = View.GONE
                }
            }
        })

        passEt.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable) {
                if (passEt.text.isEmpty()) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
                } else if (passEt.text.length > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }

            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                passEt.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null)
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                passwordError.visibility = View.GONE
                if (passEt.text.length > 4) {
                    passEt.setCompoundDrawablesWithIntrinsicBounds(null, null,
                        ContextCompat.getDrawable(applicationContext, R.drawable.ic_check), null)
                }
            }
        })
    }

    private fun checkInput() {
        val email = emailEt.text.toString().trim()
        val password = passEt.text.toString().trim()

        when {
            email.isEmpty() -> {
                emailError.visibility = View.VISIBLE
                emailError.text = "Email can't be empty"
            }
            !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> {
                emailError.visibility = View.VISIBLE
                emailError.text = "Enter a valid email"
            }
            password.isEmpty() -> {
                passwordError.visibility = View.VISIBLE
                passwordError.text = "Password can't be empty"
            }
            else -> {
                emailError.visibility = View.GONE
                passwordError.visibility = View.GONE
                signInUser(email, password)
            }
        }
    }

    private fun signInUser(email: String, password: String) {
        loadingDialog.startLoadingDialog()
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                loadingDialog.dismissDialog()
                if (task.isSuccessful) {
                    toast("Signed in successfully")
                    startActivity(Intent(this, HomeActivity::class.java))
                    finish()
                } else {
                    toast("Sign in failed: ${task.exception?.localizedMessage}")
                }
            }
    }
}
