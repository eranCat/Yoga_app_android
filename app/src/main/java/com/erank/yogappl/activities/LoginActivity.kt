package com.erank.yogappl.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.erank.yogappl.R
import com.erank.yogappl.utils.enums.TextFieldValidStates
import com.erank.yogappl.utils.extensions.setTextChangedListener
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.helpers.UserValidator
import com.erank.yogappl.utils.helpers.UserValidator.Fields.EMAIL
import com.erank.yogappl.utils.helpers.UserValidator.Fields.PASS
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.android.synthetic.main.activity_login.llProgressBar
import kotlinx.android.synthetic.main.activity_register.*

class LoginActivity : AppCompatActivity() {

    private val TAG = LoginActivity::class.java.name

    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    private val progressLayout by lazy { llProgressBar }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        UserValidator(TextFieldValidStates.EMPTY, EMAIL, PASS).apply {

            login_email.apply {
                setTextChangedListener {
                    error = validateEmail(it).errorMsg
                }
            }


            login_password.apply {
                setTextChangedListener {
                    error = validatePassword(it).errorMsg
                }
            }


            signInBtn.setOnClickListener {
                if (isNotDataValid)
                    toast("Invalid details")
                else login()
            }
        }


        signUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent, RegisterActivity.RC_REGISTER)
        }

    }

    private fun login() {

        val username = login_email.text.toString()
        val password = login_password.text.toString()

        progressLayout.visibility = View.VISIBLE
        auth.signInWithEmailAndPassword(username, password)
            .addOnCompleteListener {
                progressLayout.visibility = View.GONE
            }
            .addOnSuccessListener {
                //send result to splash screen
                setResult(RESULT_OK)
                finishActivity(SplashActivity.RC_LOGIN)
                finish()
            }
            .addOnFailureListener {
                Log.w(TAG, "createUserWithEmail:failure", it)
                toast("Authentication failed: ${it.localizedMessage}",Toast.LENGTH_LONG)
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RegisterActivity.RC_REGISTER -> {
                setResult(resultCode)
                finish()
            }
        }
    }
}