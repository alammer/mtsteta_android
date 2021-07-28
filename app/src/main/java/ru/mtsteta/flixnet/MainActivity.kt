package ru.mtsteta.flixnet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
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
            when(it.itemId){
                R.id.home-> {
                    if (!it.isChecked) loadFragment(MainScreenFragment())
                    return@setOnItemSelectedListener true
                }

                R.id.profile-> {
                    if (!it.isChecked) loadFragment(ProfileFragment())
                     return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }
    }

    override fun onBackPressed() {

    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.main_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }
}
