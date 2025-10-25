package com.example.babydevelopmenttracker.data

import androidx.annotation.StringRes
import com.example.babydevelopmenttracker.R

enum class FamilyRole(val storageValue: String, @StringRes val labelRes: Int, @StringRes val descriptionRes: Int) {
    EXPECTANT_PARENT(
        "expectant_parent",
        R.string.family_role_expectant_parent,
        R.string.family_role_expectant_parent_description
    ),
    PARTNER_SUPPORTER(
        "partner_supporter",
        R.string.family_role_partner_supporter,
        R.string.family_role_partner_supporter_description
    );

    companion object {
        fun fromStorageValue(value: String?): FamilyRole? = when (value) {
            "family_friend" -> PARTNER_SUPPORTER
            else -> entries.firstOrNull { it.storageValue == value }
        }
    }
}
