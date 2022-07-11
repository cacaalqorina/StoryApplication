package com.annisaalqorina.submissionstory.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.annisaalqorina.submissionstory.api.ApiConfig
import com.annisaalqorina.submissionstory.modeldata.SignUpBody
import com.annisaalqorina.submissionstory.response.SignUpResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SignUpViewModel : ViewModel() {

    companion object {
        private const val TAG = "SignInViewModel"
    }

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    private val _registerResponse = MutableLiveData<SignUpResponse>()
    val registerResponse: LiveData<SignUpResponse> = _registerResponse

    fun register(loginBody: SignUpBody) {
        _isLoading.value = true
        val client = ApiConfig.getMyApi().register(loginBody)
        client.enqueue(object : Callback<SignUpResponse> {
            override fun onResponse(
                call: Call<SignUpResponse>,
                response: Response<SignUpResponse>
            ) {
                _isLoading.value = false
                Log.d(TAG, "onResponse: $response")

                if (response.isSuccessful) {
                    val responseBody = response.body()
                    _registerResponse.value = responseBody
                } else {
                    try {
                        val objErr = JSONObject(response.errorBody()!!.string())
                        _snackbarText.value = objErr.getString("message")
                    } catch (e: Exception) {
                        _snackbarText.value = e.message
                    }
                }
            }

            override fun onFailure(call: Call<SignUpResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = t.message
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}