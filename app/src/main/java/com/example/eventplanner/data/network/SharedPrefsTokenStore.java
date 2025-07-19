package com.example.eventplanner.data.network;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.example.eventplanner.data.network.auth.TokenStore;

import java.util.Objects;

public class SharedPrefsTokenStore implements TokenStore {
    private static final String
            PREF_NAME = "MyAppPrefs",
            KEY_JWT = "jwt";

    private final Context context;

    public SharedPrefsTokenStore(Context context) {
        this.context = context.getApplicationContext();
    }


    @Nullable
    @Override
    public String getToken() {
        return getPreferences().getString(KEY_JWT, null);
    }


    @Override
    @SuppressLint("ApplySharedPref")
    public void setToken(@Nullable String token) {
        SharedPreferences.Editor editor = getPreferences().edit();

        if (Objects.isNull(token)) {
            editor.remove(KEY_JWT);
        } else {
            editor.putString(KEY_JWT, token);
        }

        editor.commit();
    }


    private SharedPreferences getPreferences() {
        return context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

}
