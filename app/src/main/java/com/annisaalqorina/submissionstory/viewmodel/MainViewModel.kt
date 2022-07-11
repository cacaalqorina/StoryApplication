package com.annisaalqorina.submissionstory.viewmodel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.annisaalqorina.submissionstory.api.ApiConfig
import com.annisaalqorina.submissionstory.response.GetAllStoryResponse
import com.annisaalqorina.submissionstory.response.ListStoryItem
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {

    companion object {
        private const val TAG = "UserViewModel"
    }

    private val _listStory = MutableLiveData<List<ListStoryItem>>()
    val listStory: LiveData<List<ListStoryItem>> = _listStory

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _snackbarText = MutableLiveData<String>()
    val snackbarText: LiveData<String> = _snackbarText

    fun getStories(token: String) {
        _isLoading.value = true
        val client = ApiConfig.getMyApi().getStories(token)
        client.enqueue(object : Callback<GetAllStoryResponse> {
            override fun onResponse(
                call: Call<GetAllStoryResponse>,
                response: Response<GetAllStoryResponse>
            ) {
                _isLoading.value = false
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    if (responseBody != null) {
                        _listStory.value = responseBody.listStory!!
                    }
                } else {
                    try {
                        val objErr = JSONObject(response.errorBody()!!.string())
                        _snackbarText.value = ">" + objErr.getString("message")
                    } catch (e: Exception) {
                        _snackbarText.value = e.message
                    }
                    Log.e(ContentValues.TAG, "onFailure:> ${response.message()}")
                }
            }

            override fun onFailure(call: Call<GetAllStoryResponse>, t: Throwable) {
                _isLoading.value = false
                _snackbarText.value = t.message
                Log.e(ContentValues.TAG, "onFailure: ${t.message}")
            }
        })
    }
}