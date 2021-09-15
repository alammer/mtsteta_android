package ru.mtsteta.flixnet.profile

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.button.MaterialButton
import ru.mtsteta.flixnet.R
import ru.mtsteta.flixnet.login.AuthenticationState
import ru.mtsteta.flixnet.login.LoginViewModel


class ProfileFragment : Fragment() {

    private val loginViewModel: LoginViewModel by activityViewModels()
    private lateinit var btnExit: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile_screen, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)

        val navController = findNavController()

        loginViewModel.authStatus.observe(viewLifecycleOwner, { authenticationState ->
            Log.i("LoginAuth", "auth status: $authenticationState")
            when (authenticationState) {
                AuthenticationState.UNAUTHENTICATED -> {
                    //navController.popBackStack(R.id.tab_auth, true)
                    navController.navigateUp()
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
        btnExit = view.findViewById(R.id.btnExit)
        btnExit.setOnClickListener {
            loginViewModel.signOff()
        }
    }
}

