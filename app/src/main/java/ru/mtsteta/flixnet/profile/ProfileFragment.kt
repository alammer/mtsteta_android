package ru.mtsteta.flixnet.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContentProviderCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.login.AuthenticationState
import ru.mtsteta.flixnet.login.LoginViewModel
import ru.mtsteta.flixnet.movies.MoviesViewModel

private const val ENCRYPTED_PREFS_FILE_NAME = "user_prefs"

private const val PREFERENCE_KEY_NAME = "user_settings"

class ProfileFragment : Fragment() {

    private val loginViewModel by viewModels<LoginViewModel>()

    lateinit var currentUser: ProfileDto

    private val sharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            ENCRYPTED_PREFS_FILE_NAME,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            requireContext(),
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        val currentUser = sharedPreferences.getParcelable(PREFERENCE_KEY_NAME, null)

        loginViewModel.setUser(currentUser)

        loginViewModel.authStatus.observe(viewLifecycleOwner, { authenticationState ->
            when (authenticationState) {
                AuthenticationState.NO_AUTHENTICATED -> {
                    Log.i("ProfileFragment", "NO_AUTHENTICATED")
                    navController.navigate(R.id.signUpFragment)
                }

                AuthenticationState.UNAUTHENTICATED -> {
                    Log.i("ProfileFragment", "UNAUTHENTICATED")
                    navController.navigate(R.id.loginFragment)
                }

                AuthenticationState.INVALID_AUTHENTICATION -> {
                    Log.i("ProfileFragment", "INVALID ATTEMPT")
                    navController.navigate(R.id.mainScreenFragment)
                }
                else -> Log.i("ProfileFragment", "ALREADY AUTHENTICATION")

            }
        })

    }
}

fun SharedPreferences.Editor.putParcelable(key: String, parcelable: Parcelable) {
    val json = Gson().toJson(parcelable)
    putString(key, json)
}

inline fun <reified T : Parcelable?> SharedPreferences.getParcelable(key: String, default: T): T {
    val json = getString(key, null)
    return try {
        if (json != null)
            Gson().fromJson(json, T::class.java)
        else default
    } catch (_: JsonSyntaxException) {
        default
    }
}