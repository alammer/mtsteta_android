package ru.mtsteta.flixnet.profile

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.button.MaterialButton
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.login.AuthenticationState
import ru.mtsteta.flixnet.login.LoginViewModel

private const val ENCRYPTED_PREFS_FILE_NAME = "user_prefs"

private const val PREFERENCE_KEY_NAME = "user_settings"

class ProfileFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()

    private lateinit var btnExit: MaterialButton

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

        btnExit = view.findViewById(R.id.btnExit)

        val navController = findNavController()

        val currentUser = sharedPreferences.getParcelable(PREFERENCE_KEY_NAME, null)

        loginViewModel.setUser(currentUser)

        loginViewModel.authData.observe(viewLifecycleOwner, { userPrefs ->
            userPrefs?.let {
                with(sharedPreferences.edit()) {
                    putParcelable(PREFERENCE_KEY_NAME, it as Parcelable)
                    apply()
                }
            }
        })

        loginViewModel.authStatus.observe(viewLifecycleOwner, { authenticationState ->
            when (authenticationState) {
                AuthenticationState.EMPTY_ACCOUNT -> {
                    navController.navigate(R.id.signUpFragment)
                }

                AuthenticationState.UNAUTHENTICATED -> {
                    navController.navigate(R.id.mainScreenFragment)
                }

                AuthenticationState.PROCEED_AUTHENTICATION -> {
                    navController.navigate(R.id.loginFragment)
                }
                else -> Toast.makeText(
                    context,
                    "Current autentication status is $authenticationState",
                    Toast.LENGTH_SHORT
                )

            }
        })

        btnExit.setOnClickListener {
            loginViewModel.signOff()
        }
    }
}

fun SharedPreferences.Editor.putParcelable(key: String, parcelable: Parcelable) {
    val json = Gson().toJson(parcelable)
    putString(key, json)
    apply()
}

fun SharedPreferences.getParcelable(key: String, default: Parcelable?): Parcelable? {
    val json = getString(key, null)
    return try {
        if (json != null)
            Gson().fromJson(json, ProfileDto::class.java)
        else default
    } catch (_: JsonSyntaxException) {
        default
    }
}