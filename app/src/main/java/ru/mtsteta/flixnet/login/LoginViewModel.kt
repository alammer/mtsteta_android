package ru.mtsteta.flixnet.login

import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.mtsteta.flixnet.profile.ProfileDto

@Keep
enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, EMPTY_ACCOUNT, PROCEED_AUTHENTICATION, INVALID_ATTEMPT
}

class LoginViewModel : ViewModel() {

    private var user: ProfileDto? = null
    private var initialPrefs: ProfileDto? = null
    val authData: LiveData<ProfileDto?> get() = _authData
    private val _authData = MutableLiveData<ProfileDto?>()
    val authStatus: LiveData<AuthenticationState> get() = _authStatus
    private val _authStatus = MutableLiveData<AuthenticationState>()

    fun setUser(currentUser: Parcelable?) {
        if (user == null) {
            _authStatus.value = currentUser?.let {
                initialPrefs = it as ProfileDto
                AuthenticationState.PROCEED_AUTHENTICATION
            } ?: AuthenticationState.EMPTY_ACCOUNT
        } else {
            _authStatus.value = AuthenticationState.AUTHENTICATED
        }
    }

    fun signOff() {
        user = null
        _authStatus.value = AuthenticationState.UNAUTHENTICATED
    }

    fun createUser(newUser: ProfileDto) {
        user = newUser
        _authStatus.value = AuthenticationState.AUTHENTICATED
        _authData.value = user
    }

    fun loginUser(userId: String, password: String) {
        if ((userId == initialPrefs?.userName || userId == initialPrefs?.email) && password == initialPrefs?.password) {
            _authStatus.value = AuthenticationState.AUTHENTICATED
            user = initialPrefs
        } else {
            _authStatus.value = AuthenticationState.INVALID_ATTEMPT
        }
    }
}