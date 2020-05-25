package com.erank.yogappl.utils.helpers

import android.content.Context
import android.content.Intent
import com.erank.yogappl.ui.activities.login.LoginActivity
import com.google.firebase.auth.FirebaseAuth

class AuthHelper(
    val mAuth: FirebaseAuth,
    val context: Context
) {

    val currentUser
        get() = mAuth.currentUser

    val isFbUserConnected: Boolean
        get() = (currentUser == null)

    private fun openLogin() {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun signOut() {
        mAuth.signOut()
        openLogin()
    }

    fun createUser(email: String, pass: String) =
        mAuth.createUserWithEmailAndPassword(email, pass)

}