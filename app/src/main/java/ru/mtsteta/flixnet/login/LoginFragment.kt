package ru.mtsteta.flixnet.login

import android.os.Bundle
import android.util.Log
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

class LoginFragment : Fragment() {

    private lateinit var userId: TextView
    private lateinit var userPass: TextView
    private lateinit var btnLogin: MaterialButton
    private lateinit var navController: NavController

    private val loginViewModel: LoginViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        navController = findNavController()

        loginViewModel.authStatus.observe(viewLifecycleOwner, { authenticationState ->
            when (authenticationState) {
                AuthenticationState.AUTHENTICATED -> {
                    navController.navigate(R.id.actionLoginToProfile)
                }
                AuthenticationState.INVALID_ATTEMPT -> Toast.makeText(
                    context,
                    "Wrong password or username/email",
                    Toast.LENGTH_SHORT
                )
                    .show()
                else -> Toast.makeText(
                    context,
                    "Current autentication status is $authenticationState",
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }

    private fun initViews(view: View) {
        userId = view.findViewById(R.id.etUserID)
        userPass = view.findViewById(R.id.etUserPassword)
        btnLogin = view.findViewById(R.id.btnLogin)

        btnLogin.setOnClickListener {
            loginViewModel.loginUser(userId.text.toString(), userPass.text.toString())
        }
    }
}