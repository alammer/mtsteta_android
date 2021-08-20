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
import com.google.android.gms.tasks.OnCompleteListener
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

        createNotificationChannel()

        subscribeToFCMTopic()

        getFCMtoken()

        bottomNavigationView = findViewById(R.id.bottomNavView)

        val homeMenuItem = bottomNavigationView.menu.findItem(R.id.home)

        val profileMenuItem = bottomNavigationView.menu.findItem(R.id.profile)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment

        navController = navHostFragment.navController

        bottomNavigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.detailFragment -> bottomNavigationView.menu.setGroupCheckable(0, false, true)

                R.id.mainScreenFragment -> {
                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    homeMenuItem?.run { isChecked = true }
                }

                R.id.profileFragment -> {
                    bottomNavigationView.menu.setGroupCheckable(0, true, true)
                    profileMenuItem?.run { isChecked = true }
                }
            }
        }

        bottomNavigationView.setOnItemSelectedListener {
            val fromFragment = navController.currentDestination?.id
            when (it.itemId) {
                R.id.home -> {
                    when (fromFragment) {
                        R.id.detailFragment -> navController.navigate(R.id.actionDetailToMain)
                        R.id.profileFragment -> navController.navigate(R.id.actionProfileToMain)
                        R.id.signUpFragment -> navController.navigate(R.id.actionSignUpToMain)
                        R.id.loginFragment -> navController.navigate(R.id.actionLoginToMain)
                    }
                    return@setOnItemSelectedListener true
                }

                R.id.profile -> {
                    if (fromFragment == R.id.detailFragment) {
                        navController.navigate(R.id.actionDetailToProfile)
                    } else {
                        if (!it.isChecked) {
                            navController.navigate(R.id.actionMainToProfile)
                        }
                    }
                    return@setOnItemSelectedListener true
                }
            }
            return@setOnItemSelectedListener false
        }

    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Create channel to show notifications.
            val channelId = getString(R.string.default_notification_channel_id)
            val channelName = getString(R.string.default_notification_channel_name)
            val notificationManager = getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(
                NotificationChannel(
                    channelId,
                    channelName, NotificationManager.IMPORTANCE_LOW
                )
            )
        }
    }

    private fun subscribeToFCMTopic() {
        Firebase.messaging.subscribeToTopic(getString(R.string.default_notification_topic))
            .addOnCompleteListener { task ->
                val msg = if (task.isSuccessful) "success subscribe" else "complitely failed"
                Log.i("MainActivity", "Function called: FCM subscribe to topic : $msg")
            }
    }

    private fun getFCMtoken() {
        Firebase.messaging.token.addOnCompleteListener(OnCompleteListener { task ->
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
        })
    }
}
