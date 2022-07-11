package com.annisaalqorina.submissionstory

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.annisaalqorina.submissionstory.response.LoginResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DataPreferences private constructor(private val dataStore: DataStore<Preferences>) {

    companion object {
        private val USER_ID_KEY = stringPreferencesKey("id")
        private val NAME_KEY = stringPreferencesKey("name")
        private val TOKEN_KEY = stringPreferencesKey("token")

        @Volatile
        private var INSTANCE: DataPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): DataPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = DataPreferences(dataStore)
                INSTANCE = instance
                instance
            }
        }
    }

    fun getDataSetting(): Flow<LoginResult> {
        return dataStore.data.map { preferences ->
            LoginResult(
                userId = preferences[USER_ID_KEY] ?: "",
                name = preferences[NAME_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: ""
            )
        }
    }

    suspend fun saveDataSetting(dataSetting: LoginResult) {
        dataStore.edit { preferences ->
            preferences[USER_ID_KEY] = dataSetting.userId
            preferences[NAME_KEY] = dataSetting.name
            preferences[TOKEN_KEY] = dataSetting.token
        }
    }

    suspend fun clearDataSetting() {
        dataStore.edit {
            it.clear()
        }
    }
}