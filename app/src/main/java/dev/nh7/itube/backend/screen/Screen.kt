package dev.nh7.itube.backend.screen

import dev.nh7.itube.R

enum class Screen(val displayName: Int, val icon: Int) {

    SEARCH(R.string.screen_search, R.drawable.icon_search),
    LIBRARY(R.string.screen_library, R.drawable.icon_library),
    PLAYER(R.string.screen_player, R.drawable.icon_player),
    SETTINGS(R.string.screen_settings, R.drawable.icon_settings);

}