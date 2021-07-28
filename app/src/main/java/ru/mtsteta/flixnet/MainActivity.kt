package ru.mtsteta.flixnet

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import ru.mtsteta.flixnet.fakeRepo.MoviesDataSourceImpl
import ru.mtsteta.flixnet.genres.GenreClickListener
import ru.mtsteta.flixnet.genres.GenreListAdapter
import ru.mtsteta.flixnet.movies.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportFragmentManager.beginTransaction()
            .add(R.id.main_container, MainScreenFragment())
            .commit()
    }


}
