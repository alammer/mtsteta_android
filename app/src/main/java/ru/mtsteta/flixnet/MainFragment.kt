package ru.mtsteta.flixnet

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.mtsteta.flixnet.extensions.setupWithNavController

class MainFragment : Fragment(R.layout.fragment_main) {

    private lateinit var bottomNavView: BottomNavigationView

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        super.onViewStateRestored(savedInstanceState)

        setupBottomNavigationBar(view)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (savedInstanceState == null) {
            setupBottomNavigationBar(view)
        }
    }

    private fun setupBottomNavigationBar(view: View?) {
        val navGraphIds = listOf(
            R.navigation.movies_nav_graph,
            R.navigation.auth_nav_graph
        )

        view?.run {
            bottomNavView = findViewById(R.id.bottomNavView)

            bottomNavView.setupWithNavController(
                navGraphIds = navGraphIds,
                fragmentManager = childFragmentManager,
                containerId = R.id.fragment_main_nav_host_container,
                intent = requireActivity().intent
            )
        }
    }

}