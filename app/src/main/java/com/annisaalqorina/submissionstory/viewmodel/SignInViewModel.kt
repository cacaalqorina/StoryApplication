package com.annisaalqorina.submissionstory.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.annisaalqorina.submissionstory.api.ApiConfig
import com.annisaalqorina.submissionstory.modeldata.SignInBody
import com.annisaalqorina.submissionstory.response.SignInResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignInViewModel : ViewModel() {

    companion object {
        private const val TAG = "SignInViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    private val _signinResponse = MutableLiveData<SignInResponse>()
    val loginResponse: LiveData<SignInResponse> = _signinResponse

    fun login(loginBody: SignInBody) {
        _isLoading.value = true
        val client = ApiConfig.getMyApi().login(loginBody)
        client.enqueue(object : Callback<SignInResponse> {
            override fun onResponse(
                call: Call<SignInResponse>,
                response: Response<SignInResponse>
            ) {
                _isLoading.value = false
                Log.d(TAG, "onResponse: $response")
                if (response.isSuccessful) {
                    _signinResponse.value = response.body()
                } else {
                    try {
                        val objErr = JSONObject(response.errorBody()!!.string())
                        _snackbarText.value = objErr.getString("message")
                    } catch (e: Exception) {
                        _snackbarText.value = e.message
                    }
                }
            }

            override fun onFailure(call: Call<SignInResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = t.message
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}