package com.example.babydevelopmenttracker.ui.theme

import androidx.annotation.StringRes
import com.example.babydevelopmenttracker.R

/**
 * Available user-selectable color themes.
 */
enum class ThemePreference(
    val storageValue: String,
    @StringRes val titleRes: Int,
    @StringRes val descriptionRes: Int
) {
    Neutral(
        storageValue = "neutral",
        titleRes = R.string.theme_option_neutral_title,
        descriptionRes = R.string.theme_option_neutral_description
    ),
    Blossom(
        storageValue = "blossom",
        titleRes = R.string.theme_option_blossom_title,
        descriptionRes = R.string.theme_option_blossom_description
    ),
    Skyline(
        storageValue = "skyline",
        titleRes = R.string.theme_option_skyline_title,
        descriptionRes = R.string.theme_option_skyline_description
    );

    companion object {
        fun fromStorageValue(value: String?): ThemePreference {
            return entries.firstOrNull { it.storageValue == value } ?: Neutral
        }
    }
}
