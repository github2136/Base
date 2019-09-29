package com.github2136.basemvvm

import android.content.SharedPreferences

/**
 * Created by YB on 2019/9/26
 */
fun SharedPreferences.getString(key: String) = getString(key, null)
fun SharedPreferences.getInt(key: String) = getInt(key, 0)
fun SharedPreferences.getLong(key: String) = getLong(key, 0)
fun SharedPreferences.getFloat(key: String) = getFloat(key, 0F)
fun SharedPreferences.getBoolean(key: String) = getBoolean(key, false)
fun SharedPreferences.getStringSet(key: String) = getStringSet(key, null)
