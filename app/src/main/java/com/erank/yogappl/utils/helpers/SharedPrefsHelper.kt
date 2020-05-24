package com.erank.yogappl.utils.helpers

import android.content.Context
import com.erank.yogappl.data.models.User

class SharedPrefsHelper private constructor() {

    class Builder private constructor(context: Context, name: String) {
        private val prefs = context.getSharedPreferences(name, Context.MODE_PRIVATE)

        constructor(context: Context, user: User)
                : this(context, "user${user.id}")

        constructor(context: Context)
                : this(context, "app")


        fun put(key: String, data: Long): Builder {
            prefs.edit().putLong(key, data).apply()
            return this
        }

        fun put(key: String, data: Float): Builder {
            prefs.edit().putFloat(key, data).apply()
            return this
        }

        fun remove(key: String): Builder {
            prefs.edit().remove(key).apply()
            return this
        }

        fun getLong(key: String): Long? {
            if (prefs.contains(key))
                return prefs.getLong(key, 0)
            return null
        }

        fun getInt(key: String, defaultValue: Int) =
            prefs.getInt(key, defaultValue)

        fun getFloat(key: String): Float? {
            if (prefs.contains(key).not()) return null
            return prefs.getFloat(key, 0f)
        }

        fun getString(key: String, defaultValue: String?): String? =
            prefs.getString(key, defaultValue)
    }
}