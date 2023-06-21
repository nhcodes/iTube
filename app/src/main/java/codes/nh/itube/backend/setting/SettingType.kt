package codes.nh.itube.backend.setting

sealed class SettingType(val default: Any) {
    class Switch(default: Boolean) : SettingType(default)
    class Slider(default: Int, val range: ClosedRange<Int>, val step: Int) : SettingType(default)
    class Select(default: String, val options: Array<String>) : SettingType(default)
}