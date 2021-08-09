package ru.mtsteta.flixnet

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.mtsteta.flixnet.detail.DetailFragment
import ru.mtsteta.flixnet.movies.*
import ru.mtsteta.flixnet.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavView)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, MainScreenFragment())
                .commit()
        } else {
            when (supportFragmentManager.findFragmentById(R.id.main_container)) {
                is DetailFragment -> bottomNavigationView.menu.setGroupCheckable(0, false, true)
            }
        }

        supportFragmentManager.addOnBackStackChangedListener {
            when (supportFragmentManager.findFragmentById(R.id.main_container)) {
                is DetailFragment -> bottomNavigationView.menu.setGroupCheckable(0, false, true)
                is MainScreenFragment -> {
                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    bottomNavigationView.menu.findItem(R.id.home)?.run { isChecked = true }
                }
                is ProfileFragment -> {
                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    bottomNavigationView.menu.findItem(R.id.profile)?.run { isChecked = true }
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener {
            val fromDetailFragment =
                supportFragmentManager.findFragmentById(R.id.main_container) is DetailFragment
            when (it.itemId) {
                R.id.home -> {
                    if (fromDetailFragment) {
                        supportFragmentManager.popBackStack()
                        loadFragment(MainScreenFragment())
                    } else {
                        if (!it.isChecked) {
                            loadFragment(MainScreenFragment())
                        }
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.profile -> {
                    if (!it.isChecked) {
                        loadFragment(ProfileFragment(), fromDetailFragment)
                    }
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
}
