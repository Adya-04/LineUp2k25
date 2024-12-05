package com.example.lineup2025.utils

import android.content.Context
import com.example.lineup2025.utils.Constants.PREFS_TOKEN_FILE
import com.example.lineup2025.utils.Constants.SCANNED_QR_CODES
import com.example.lineup2025.utils.Constants.USER_NAME
import com.example.lineup2025.utils.Constants.USER_TOKEN
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class TokenManager @Inject constructor(@ApplicationContext context: Context) {
    private var prefs = context.getSharedPreferences(PREFS_TOKEN_FILE, Context.MODE_PRIVATE)

    fun saveToken(token : String, name: String){
        val editor = prefs.edit()
        editor.putString(USER_TOKEN, token)
        editor.putString(USER_NAME, name)
        editor.apply()
    }

    fun getToken() : String? {
        return prefs.getString(USER_TOKEN, null)
    }

    fun getName() : String? {
        return prefs.getString(USER_NAME,null)
    }

    fun saveScannedQRCodes(codes: Set<String>) {
        val editor = prefs.edit()
        editor.putStringSet(SCANNED_QR_CODES, codes) // Convert to HashSet
        editor.apply()
    }

    fun getScannedQRCodes(): Set<String> {
        return prefs.getStringSet(SCANNED_QR_CODES, emptySet()) ?: emptySet()
    }

}