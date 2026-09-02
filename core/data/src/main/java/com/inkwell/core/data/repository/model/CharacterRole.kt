package com.inkwell.core.data.repository.model

enum class CharacterRole(val displayName: String) {
    PROTAGONIST("Protagonist"),
    ANTAGONIST("Antagonist"),
    SUPPORTING("Supporting"),
    MINOR("Minor");

    companion object {
        fun fromString(value: String): CharacterRole =
            entries.find { it.name.equals(value, ignoreCase = true) } ?: MINOR
    }
}
