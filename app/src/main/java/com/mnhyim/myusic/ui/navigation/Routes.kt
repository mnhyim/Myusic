package com.mnhyim.myusic.ui.navigation

import kotlinx.serialization.Serializable

sealed class Routes {

    @Serializable
    data object Home: Routes()
}