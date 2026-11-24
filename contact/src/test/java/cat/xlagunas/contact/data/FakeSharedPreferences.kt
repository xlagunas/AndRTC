package cat.xlagunas.contact.data

import android.content.SharedPreferences

class FakeSharedPreferences : SharedPreferences {
    private val values = mutableMapOf<String, Any?>()

    override fun getAll(): MutableMap<String, *> = values.toMutableMap()

    override fun getString(
        key: String,
        defValue: String?,
    ): String? = values[key] as? String ?: defValue

    @Suppress("UNCHECKED_CAST")
    override fun getStringSet(
        key: String,
        defValues: MutableSet<String>?,
    ): MutableSet<String>? = values[key] as? MutableSet<String> ?: defValues

    override fun getInt(
        key: String,
        defValue: Int,
    ): Int = values[key] as? Int ?: defValue

    override fun getLong(
        key: String,
        defValue: Long,
    ): Long = values[key] as? Long ?: defValue

    override fun getFloat(
        key: String,
        defValue: Float,
    ): Float = values[key] as? Float ?: defValue

    override fun getBoolean(
        key: String,
        defValue: Boolean,
    ): Boolean = values[key] as? Boolean ?: defValue

    override fun contains(key: String): Boolean = values.containsKey(key)

    override fun edit(): SharedPreferences.Editor = FakeEditor()

    override fun registerOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit

    override fun unregisterOnSharedPreferenceChangeListener(listener: SharedPreferences.OnSharedPreferenceChangeListener) = Unit

    private inner class FakeEditor : SharedPreferences.Editor {
        private val staged = mutableMapOf<String, Any?>()
        private val removed = mutableSetOf<String>()
        private var clearRequested = false

        private fun stage(
            key: String,
            value: Any?,
        ) = apply {
            removed.remove(key)
            staged[key] = value
        }

        override fun putString(
            key: String,
            value: String?,
        ) = stage(key, value)

        override fun putStringSet(
            key: String,
            values: MutableSet<String>?,
        ) = stage(key, values)

        override fun putInt(
            key: String,
            value: Int,
        ) = stage(key, value)

        override fun putLong(
            key: String,
            value: Long,
        ) = stage(key, value)

        override fun putFloat(
            key: String,
            value: Float,
        ) = stage(key, value)

        override fun putBoolean(
            key: String,
            value: Boolean,
        ) = stage(key, value)

        override fun remove(key: String) =
            apply {
                staged.remove(key)
                removed.add(key)
            }

        override fun clear() = apply { clearRequested = true }

        override fun commit(): Boolean {
            if (clearRequested) values.clear()
            removed.forEach { values.remove(it) }
            values.putAll(staged)
            return true
        }

        override fun apply() {
            commit()
        }
    }
}
