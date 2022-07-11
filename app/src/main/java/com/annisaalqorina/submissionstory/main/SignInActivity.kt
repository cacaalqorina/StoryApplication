package com.annisaalqorina.submissionstory.main

import android.content.ContentValues
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.lifecycle.ViewModelProvider
import com.annisaalqorina.submissionstory.DataPreferences
import com.annisaalqorina.submissionstory.R
import com.annisaalqorina.submissionstory.databinding.ActivitySignInBinding
import com.annisaalqorina.submissionstory.modeldata.SignInBody
import com.annisaalqorina.submissionstory.viewmodel.DataViewModel
import com.annisaalqorina.submissionstory.viewmodel.SignInViewModel
import com.annisaalqorina.submissionstory.viewmodel.ViewModelFactory
import com.google.android.material.snackbar.Snackbar

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class SignInActivity : AppCompatActivity() {

    private lateinit var activitySignInBinding: ActivitySignInBinding

    private val signInViewModel: SignInViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activitySignInBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(activitySignInBinding.root)

        val dataPreferences = DataPreferences.getInstance(dataStore)
        val dataViewModel = ViewModelProvider(
            this,
            ViewModelFactory(dataPreferences)
        )[DataViewModel::class.java]

        signInViewModel.isLoading.observe(this) {
            setProgrssDIalog(it)
        }

        signInViewModel.loginResponse.observe(this) {
            dataViewModel.saveDataSession(it.signinResult)
            Log.d(TAG, "onCreate :" + it.signinResult.token)
            startActivity(Intent(baseContext, MainActivity::class.java))
            finish()
        }

        signInViewModel.snackbarText.observe(this) {
            Snackbar.make(
                activitySignInBinding.btnSignIn, it,
                Snackbar.LENGTH_SHORT
            ).show()
        }

        title = resources.getString(R.string.login)

        setClick()
    }

    private fun setClick() {
        activitySignInBinding.apply {
            tvSignUp.setOnClickListener {
                startActivity(Intent(baseContext, SignUpActivity::class.java))
            }

            btnSignIn.setOnClickListener {
                if (android.util.Patterns.EMAIL_ADDRESS.matcher(emailAccount.text.toString())
                        .matches()
                ) {
                    if (passwordAccount.length() >= 6) doSignIn()
                    else{
                        showSnackBar(resources.getString(R.string.password_alert))
                    }
                }else{
                    showSnackBar(resources.getString(R.string.email_invalid))
                }
            }
        }
    }

    private fun setProgrssDIalog(isLoading: Boolean) {
        activitySignInBinding.progressBar.visibility =
            if (isLoading) View.VISIBLE else View.GONE
    }

    private fun showSnackBar(isiString: String) {
        Snackbar.make(
            activitySignInBinding.btnSignIn, isiString, Snackbar.LENGTH_SHORT
        ).show()
    }

    private fun doSignIn() {
        signInViewModel.login(
            SignInBody(
                activitySignInBinding.emailAccount.text.toString(),
                activitySignInBinding.passwordAccount.text.toString()
            )
        )
    }
}
