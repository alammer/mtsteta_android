package ru.mtsteta.flixnet

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import ru.mtsteta.flixnet.detail.DetailFragment
import ru.mtsteta.flixnet.fakeRepo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.movies.*
import ru.mtsteta.flixnet.profile.ProfileFragment

class MainActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .add(R.id.main_container, MainScreenFragment())
                .commit()
        }

        supportFragmentManager.addOnBackStackChangedListener {  }

        bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavView)

        bottomNavigationView.setOnItemSelectedListener{
            val fromDetailFragment = supportFragmentManager.findFragmentById(R.id.main_container) is DetailFragment
            when(it.itemId){
                R.id.home-> {
                    if (fromDetailFragment)
                    {
                        supportFragmentManager.popBackStack()
                        loadFragment(MainScreenFragment())
                    }
                    if (!it.isChecked )
                    {
                        loadFragment(MainScreenFragment())
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.profile-> {
                    if (!it.isChecked) {
                        loadFragment(ProfileFragment(), fromDetailFragment)
                    }
                     return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            bottomNavigationView.menu.getItem(0).isChecked = true
            Log.i("MainActivity", "Function called: onBackPressed()")
        }
        if (supportFragmentManager.findFragmentById(R.id.main_container) is DetailFragment) bottomNavigationView.menu.getItem(0).isChecked = true
        super.onBackPressed()
    }


    private fun loadFragment(fragment: Fragment, addToBackStack: Boolean = false) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
        if (addToBackStack) transaction.addToBackStack(null)
        transaction.commit()
    }
}
