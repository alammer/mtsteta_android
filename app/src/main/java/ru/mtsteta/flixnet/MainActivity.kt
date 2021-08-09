package ru.mtsteta.flixnet

import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

import ru.mtsteta.flixnet.movies.*

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = "test_channel_id"//getString(R.string.default_notification_channel_id)
            val channelName = "TestChannel"//getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
            )
        }

        Firebase.messaging.subscribeToTopic("teta")
            .addOnCompleteListener { task ->
                var msg = "success subscribe"
                if (!task.isSuccessful) {
                    msg = "complitely failed"
                }
                Log.i("MainActivity", "Function called: FCM subscribe to topic : $msg")
                Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
            }

        /*Firebase.messaging.getToken().addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.i("MainActivity", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            val msg = "get tocken $token"
            Log.i("MainActivity", "Function called: getToken() - $msg")
            Toast.makeText(baseContext, msg, Toast.LENGTH_SHORT).show()
        })*/

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
