package ru.mtsteta.flixnet.login

import android.content.SharedPreferences
import android.os.Parcelable
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

    private var user: ProfileDto = ProfileDto()

    val authStatus: LiveData<AuthenticationState> get() = _authStatus
    private val _authStatus = MutableLiveData<AuthenticationState>()

    fun setUser(currentUser: Parcelable?) {

        currentUser?.let {
            user = currentUser as ProfileDto
        }

        if (currentUser == null) _authStatus.value = AuthenticationState.NO_AUTHENTICATED
    }

    fun signOff() {
        user = ProfileDto()
        _authStatus.value = AuthenticationState.UNAUTHENTICATED
    }


}