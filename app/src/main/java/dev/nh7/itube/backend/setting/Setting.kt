package dev.nh7.itube.backend.setting

import dev.nh7.itube.R

enum class Setting(
    val title: Int,
    val description: Int,
    val category: Int,
    val type: SettingType
) {

    PLAY_SONG_AFTER_DOWNLOAD(
        R.string.setting_title_playsongafterdownload,
        R.string.setting_description_playsongafterdownload,
        R.string.setting_category_downloads,
        SettingType.Switch(true)
    ),

    AUTO_DOWNLOAD(
        R.string.setting_title_autodownload,
        R.string.setting_description_autodownload,
        R.string.setting_category_downloads,
        SettingType.Switch(false)
    ),

    DOWNLOAD_BUFFER(
        R.string.setting_title_downloadbuffer,
        R.string.setting_description_downloadbuffer,
        R.string.setting_category_downloads,
        SettingType.Slider(1000, 100..10000, 100)
    ),

    THEME(
        R.string.setting_title_theme,
        R.string.setting_description_theme,
        R.string.setting_category_theme,
        SettingType.Select("Auto", arrayOf("Auto", "Dark", "Light"))
    ),

    RESET_BROWSER(
        R.string.setting_title_resetbrowser,
        R.string.setting_description_resetbrowser,
        R.string.setting_category_browser,
        SettingType.Switch(false)
    ),

    UPDATES(
        R.string.setting_title_updates,
        R.string.setting_description_updates,
        R.string.setting_category_app,
        SettingType.Switch(true)
    );

    companion object {
        fun getSettingValueFromSettingsMap(
            setting: Setting,
            settings: Map<String, String>
        ): String {
            return settings[setting.name] ?: setting.type.default.toString()
        }
    }

}

