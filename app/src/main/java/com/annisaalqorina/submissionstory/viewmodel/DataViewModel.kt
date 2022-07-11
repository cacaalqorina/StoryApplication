package com.annisaalqorina.submissionstory.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.annisaalqorina.submissionstory.DataPreferences
import com.annisaalqorina.submissionstory.response.LoginResult

import kotlinx.coroutines.launch

class DataViewModel (private val pref: DataPreferences) : ViewModel() {

    fun getDataSession(): LiveData<LoginResult> {
        return pref.getDataSetting().asLiveData()
    }

    fun saveDataSession(dataSetting: LoginResult) {
        viewModelScope.launch {
            pref.saveDataSetting(dataSetting)
        }
    }

    fun clearDataSession() {
        viewModelScope.launch {
            pref.clearDataSetting()
        }
    }
}