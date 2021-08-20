package ru.mtsteta.flixnet.login

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.activity.addCallback
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.profile.ProfileDto

class SignUpFragment : Fragment() {

    private lateinit var userName: TextView
    private lateinit var userMail: TextView
    private lateinit var userPhone: TextView
    private lateinit var userPass: TextView
    private lateinit var confirmPass: TextView
    private lateinit var btnCreate: MaterialButton
    private lateinit var navController: NavController

    private val loginViewModel: LoginViewModel by activityViewModels()

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

        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner) {
            navController.navigate(R.id.actionSignUpToMain)
        }

        loginViewModel.authStatus.observe(viewLifecycleOwner, { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    navController.popBackStack()
                }
                else -> Toast.makeText(
                    context,
                    "Current autentication status is $authenticationState",
                    Toast.LENGTH_SHORT
                )
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