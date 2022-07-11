package com.annisaalqorina.submissionstory.main

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.annisaalqorina.submissionstory.DataPreferences
import com.annisaalqorina.submissionstory.databinding.ActivitySplashScreenBinding
import com.annisaalqorina.submissionstory.viewmodel.DataViewModel
import com.annisaalqorina.submissionstory.viewmodel.ViewModelFactory

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        private val DELLAY_MILLIS = 2000L
    }

    private lateinit var activitySplashScreenBinding: ActivitySplashScreenBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySplashScreenBinding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(activitySplashScreenBinding.root)

        hideSystemUI()

        val dataPreferences = DataPreferences.getInstance(dataStore)
        val dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataPreferences)
        )[DataViewModel::class.java]

        var isSignIn = false

        dataViewModel.getDataSession().observe(
            this
        ) { isSignIn = it.userId.isNotEmpty() }

        Handler(Looper.getMainLooper()).postDelayed({
            if (isSignIn) {
                startActivity(Intent(this, MainActivity::class.java))
                /*startActivity(Intent(this, LoginActivity::class.java))*/
            } else {
                startActivity(Intent(this, SignInActivity::class.java))
            }
            finish()
        }, DELLAY_MILLIS)
    }

    private fun hideSystemUI() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }
}