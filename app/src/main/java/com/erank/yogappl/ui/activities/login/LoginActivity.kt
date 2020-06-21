package com.erank.yogappl.ui.activities.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.afollestad.vvalidator.form
import com.afollestad.vvalidator.form.Form
import com.erank.yogappl.R
import com.erank.yogappl.ui.activities.register.RegisterActivity
import com.erank.yogappl.ui.custom_views.ProgressDialog
import com.erank.yogappl.utils.App
import com.erank.yogappl.utils.extensions.txt
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

    private lateinit var validationForm : Form

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        (application as App).getAppComponent().inject(this)

        initValidator()

        signUpBtn.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivityForResult(intent, RC_REGISTER)
        }

    }

    private fun initValidator() {
        validationForm = form {
            input(login_email) {
                isNotEmpty().description(R.string.fill)
                isEmail().description(R.string.invalid_email)
            }
            input(login_password) {
                isNotEmpty().description(R.string.fill)
                length().atLeast(6)
                    .description(R.string.atLeast6)
            }
            submitWith(signInBtn) {
                login()
            }
        }
    }

    private fun login() {

        val username = login_email.txt
        val password = login_password.txt

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