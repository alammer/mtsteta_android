package ru.mtsteta.flixnet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.mtsteta.flixnet.movies.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavView)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailFragment -> bottomNavigationView.menu.setGroupCheckable(0, false, true)

                R.id.mainScreenFragment -> {
                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    bottomNavigationView.menu.findItem(R.id.home)?.run { isChecked = true }
                }

                R.id.profileFragment -> {
                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    bottomNavigationView.menu.findItem(R.id.profile)?.run { isChecked = true }
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener {
            val fromDetailFragment = navController.currentDestination?.id == R.id.detailFragment
            when (it.itemId) {
                R.id.home -> {
                    if (fromDetailFragment) {
                        navController.navigate(R.id.action_detailFragment_to_mainScreenFragment)
                    } else {
                        if (!it.isChecked) navController.navigate(R.id.action_profileFragment_to_mainScreenFragment)

                    }
                    return@setOnItemSelectedListener true
                }
                R.id.profile -> {
                    if (fromDetailFragment) {
                        navController.navigate(R.id.action_detailFragment_to_profileFragment)
                    } else {
                        if (!it.isChecked) navController.navigate(R.id.action_mainScreenFragment_to_profileFragment)
                    }
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }
}
