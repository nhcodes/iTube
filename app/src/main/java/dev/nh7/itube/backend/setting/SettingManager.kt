package dev.nh7.itube.backend.setting

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.runtime.snapshots.SnapshotStateMap
import androidx.compose.runtime.toMutableStateMap
import dev.nh7.itube.backend.utils.async

class SettingManager(private val context: Context) {

    private var currentPreferencesStateMap = loadPreferences()

    private fun getSharedPreferences(): SharedPreferences {
        return context.getSharedPreferences("settings", Context.MODE_PRIVATE)
    }

    private fun loadPreferences(): SnapshotStateMap<String, String> {
        //async {
        return getSharedPreferences().all.entries
            .map { it.key to it.value.toString() }.toMutableStateMap()
        //}
    }

    fun getSettings(): Map<String, String> {
        return currentPreferencesStateMap
    }

    fun getSetting(setting: Setting): String {
        return Setting.getSettingValueFromSettingsMap(setting, currentPreferencesStateMap)
    }

    fun setSetting(key: String, value: String) {
        async {
            val sharedPreferences = getSharedPreferences()
            val editor = sharedPreferences.edit()
            editor.putString(key, value)
            val success = editor.commit()
            if (success) currentPreferencesStateMap[key] = value
        }
    }

}