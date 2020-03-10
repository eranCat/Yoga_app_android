package com.erank.yogappl.utils.helpers

import android.content.Context
import android.content.Intent
import com.erank.yogappl.activities.LoginActivity
import com.erank.yogappl.utils.data_source.DataSource
import com.google.firebase.auth.FirebaseAuth

object AuthHelper {

    private val mAuth = FirebaseAuth.getInstance()
    val currentUser = mAuth.currentUser

    private fun openLogin(context: Context) {
        val intent = Intent(context, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        context.startActivity(intent)
    }

    fun signOut(context: Context) {
        mAuth.signOut()
        DataSource.currentUser = null
        openLogin(context)
    }

    fun createUser(email: String, pass: String) =
        mAuth.createUserWithEmailAndPassword(email, pass)


}