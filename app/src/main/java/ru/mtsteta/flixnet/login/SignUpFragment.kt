package ru.mtsteta.flixnet.login

import android.content.SharedPreferences
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKeys
import com.google.android.material.button.MaterialButton
import com.google.gson.GsonBuilder
import com.google.gson.JsonSyntaxException
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.profile.*

private val jsonPrefsObject by lazy {
    GsonBuilder()
        .serializeNulls()
        .create()
}

class SignUpFragment : Fragment() {

    private lateinit var userName: TextView
    private lateinit var userMail: TextView
    private lateinit var userPhone: TextView
    private lateinit var userPass: TextView
    private lateinit var confirmPass: TextView
    private lateinit var btnCreate: MaterialButton
    private lateinit var navController: NavController

    private val loginViewModel: LoginViewModel by activityViewModels()

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
        return inflater.inflate(R.layout.fragment_signup_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        navController = findNavController()

        loginViewModel.setUser(sharedPreferences.getParcelable(PREFERENCE_KEY_NAME, null))

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
                AuthenticationState.AUTHENTICATED -> {
                    navController.navigate(R.id.actionSignUpToProfile)
                }
                AuthenticationState.PROCEED_AUTHENTICATION -> {
                    navController.navigate(R.id.actionSignUpToLogin)
                }
                else -> Toast.makeText(
                    context,
                    "Current autentication status is $authenticationState",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun initViews(view: View) {
        userName = view.findViewById(R.id.etUserName)
        userMail = view.findViewById(R.id.etNewEmail)
        userPhone = view.findViewById(R.id.etNewPhone)
        userPhone = view.findViewById(R.id.etNewPhone)
        userPass = view.findViewById(R.id.etNewPassword)
        confirmPass = view.findViewById(R.id.etConfirmNewPassword)
        btnCreate = view.findViewById(R.id.btnCreateAccount)
        btnCreate.setOnClickListener {
            loginViewModel.createUser(
                ProfileDto(
                    userName = userName.text.toString(),
                    email = userMail.text.toString(),
                    phone = userPhone.text.toString(),
                    password = userPass.text.toString()
                )
            )
        }
    }
}

fun SharedPreferences.Editor.putParcelable(key: String, parcelable: Parcelable) {
    val json = jsonPrefsObject.toJson(parcelable)
    putString(key, json)
    apply()
}

fun SharedPreferences.getParcelable(key: String, default: Parcelable?): Parcelable? {
    val json = getString(key, null)
    return try {
        if (json != null) {
            jsonPrefsObject.fromJson(json, ProfileDto::class.java)
        } else {
            default
        }
    } catch (_: JsonSyntaxException) {
        default
    }
}

private const val ENCRYPTED_PREFS_FILE_NAME = "user_prefs"
private const val PREFERENCE_KEY_NAME = "user_settings"