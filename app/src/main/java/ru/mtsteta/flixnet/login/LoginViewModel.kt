package ru.mtsteta.flixnet.login

import android.content.SharedPreferences
import android.os.Parcelable
import android.util.Log
import androidx.annotation.Keep
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.mtsteta.flixnet.profile.ProfileDto
import ru.mtsteta.flixnet.repo.RefreshDataStatus

@Keep
enum class AuthenticationState {
    AUTHENTICATED, UNAUTHENTICATED, INVALID_AUTHENTICATION, NO_AUTHENTICATED
}

class LoginViewModel : ViewModel() {

    private var user: ProfileDto? = null

    val authData: LiveData<ProfileDto?> get() = _authData
    private val _authData = MutableLiveData<ProfileDto?>()

    val authStatus: LiveData<AuthenticationState> get() = _authStatus
    private val _authStatus = MutableLiveData<AuthenticationState>()

    fun setUser(currentUser: Parcelable?) {

        if (user == null){
            Log.i("LoginViewModel", "Set user with ${currentUser.toString()}")
            _authStatus.value = currentUser?.let {
                user = it as ProfileDto
                AuthenticationState.INVALID_AUTHENTICATION
            } ?: AuthenticationState.NO_AUTHENTICATED
        } else _authStatus.value = AuthenticationState.AUTHENTICATED
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
        if ( (userId == user?.userName || userId == user?.email) && password == user?.password ) _authStatus.value = AuthenticationState.AUTHENTICATED
        else _authStatus.value = AuthenticationState.INVALID_AUTHENTICATION
    }

}