package com.erank.yogappl.ui.activities.login

import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import javax.inject.Inject

class LoginViewModel @Inject constructor(private val auth: FirebaseAuth) : ViewModel() {
    fun signIn(username: String, password: String) =
        auth.signInWithEmailAndPassword(username, password)
}