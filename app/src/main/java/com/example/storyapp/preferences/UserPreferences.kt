package com.example.storyapp.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.storyapp.data.remote.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userDataStore: DataStore<Preferences> by preferencesDataStore(name = "session")
class UserPreferences private constructor(private val dataStore: DataStore<Preferences>){
    private val IS_LOGGED_IN = booleanPreferencesKey("is_logged_in")
    private val EMAIL_KEY = stringPreferencesKey("email")
    private val TOKEN_KEY = stringPreferencesKey("token")
    
    fun getUserSession(): Flow<UserModel> {
        return dataStore.data.map { preferences -> 
            UserModel(
                email = preferences[EMAIL_KEY] ?: "",
                token = preferences[TOKEN_KEY] ?: "",
                isLoggedIn = preferences[IS_LOGGED_IN] ?: false
            )
        }
    }
    
    suspend fun saveUserSession(email: String, token: String) {
        dataStore.edit { preferences ->
            preferences[EMAIL_KEY] = email
            preferences[TOKEN_KEY] = token
            preferences[IS_LOGGED_IN] = true
        }
    }
    
    suspend fun logout() {
        dataStore.edit { preferences ->
            preferences.clear()
        }
    }
    
    companion object {
        @Volatile
        private var INSTANCE: UserPreferences? = null

        fun getInstance(dataStore: DataStore<Preferences>): UserPreferences {
            return INSTANCE ?: synchronized(this) {
                INSTANCE
                    ?: UserPreferences(dataStore).also { INSTANCE = it }
            }
        }
    }
}