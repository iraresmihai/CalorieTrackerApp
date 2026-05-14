package com.rares.calorietracker.viewmodel

import com.notes.app.model.service.impl.AccountServiceRepository
import kotlinx.coroutines.flow.MutableStateFlow

class SignInViewModel constructor(
    private val accountService: AccountServiceRepository
) {
    val email = MutableStateFlow("")
    val password = MutableStateFlow("")
    val confirmPassword = MutableStateFlow("")

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
            openAndPopUp("home", "signIn")
        }
    }

    fun onSignUpClickGoTo(openAndPopUp: (String, String) -> Unit) {
        openAndPopUp("signUp", "signIn")
    }

    fun onSignUpClick(openAndPopUp: (String, String) -> Unit) {
        launchCatching {
            if (password.value != confirmPassword.value) {
                throw Exception("Passwords do not match")
            }

            accountService.signUp(email.value, password.value)
            openAndPopUp("home", "signUp")
        }
    }
}