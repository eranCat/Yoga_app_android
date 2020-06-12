package com.erank.yogappl.ui.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.erank.yogappl.R
import com.erank.yogappl.data.enums.TextFieldValidStates
import com.erank.yogappl.ui.activities.register.RegisterActivity
import com.erank.yogappl.ui.custom_views.ProgressDialog
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.extensions.setTextChangedListener
import com.erank.yogappl.utils.extensions.toast
import com.erank.yogappl.utils.helpers.UserValidator
import com.erank.yogappl.utils.helpers.UserValidator.Fields.EMAIL
import com.erank.yogappl.utils.helpers.UserValidator.Fields.PASS
import kotlinx.android.synthetic.main.activity_login.*
import javax.inject.Inject

class LoginActivity : AppCompatActivity() {

    companion object {
        private val TAG = LoginActivity::class.java.name
        const val RC_REGISTER = 243
    }

    private val progressDialog by lazy { ProgressDialog(this) }

    @Inject
    lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        (application as App).getAppComponent().inject(this)

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
                    toast(R.string.invalid_details)
                else login()
            }
        }


        signUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent, RC_REGISTER)
        }

    }

    private fun login() {

        val username = login_email.text.toString()
        val password = login_password.text.toString()

        progressDialog.show()
        viewModel.signIn(username, password)
            .addOnCompleteListener { progressDialog.dismiss() }
            .addOnSuccessListener {
                //send result to splash screen
                setResult(RESULT_OK)
                finish()
            }
            .addOnFailureListener {
                Log.w(TAG, "createUserWithEmail:failure", it)
                val msg = getString(R.string.auth_failed_with, it.localizedMessage)
                AlertDialog.Builder(this)
                    .setTitle(R.string.login_failed)
                    .setMessage(msg)
                    .setPositiveButton(R.string.ok, null)
                    .show()
            }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            RC_REGISTER -> {
                setResult(resultCode)
                finish()
            }
        }
    }
}