package com.inkwell.app.ui.navigation

import kotlinx.serialization.Serializable

sealed class Screen {
    @Serializable
    object Home : Screen()

    @Serializable
    data class Editor(val novelId: Long) : Screen()

    @Serializable
    data class Characters(val novelId: Long) : Screen()

    @Serializable
    data class World(val novelId: Long) : Screen()

    @Serializable
    object Settings : Screen()

    @Serializable
    object Camera : Screen()

    @Serializable
    object Maps : Screen()
}
