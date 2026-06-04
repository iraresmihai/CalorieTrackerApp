package com.rares.calorietracker.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.notes.app.model.service.impl.AccountServiceRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch

class SignInViewModel constructor(
    private val accountService: AccountServiceRepository
) : ViewModel() {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

    private fun launchCatching(block: suspend () -> Unit) =
        viewModelScope.launch(
            CoroutineExceptionHandler { _, throwable ->
                Log.e("SignInViewModel", throwable.message ?: "Auth error", throwable)
            }
        ) {
            block()
        }

    fun updateEmail(newEmail: String) {
        email.value = newEmail
    }

    fun updatePassword(newPassword: String) {
        password.value = newPassword
    }

    fun updateConfirmPassword(newConfirmPassword: String) {
        confirmPassword.value = newConfirmPassword
    }

    fun onSignInClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            accountService.signIn(email.value, password.value)
            openAndPopUp("Home", "signIn")
        }
    }

    fun onSignUpClickGoTo(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp("signUp", "signIn")
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        Log.d("DEBUGSIGN", "doing sign up")
        launchCatching {
            if (password.value != confirmPassword.value) {
                throw Exception("Passwords do not match")
            }

            Log.d("DEBUGSIGN", "before sign up")
            accountService.signUp(email.value, password.value)
            Log.d("DEBUGSIGN", "after sign up")
            openAndPopUp("Home", "signUp")
        }
    }
}