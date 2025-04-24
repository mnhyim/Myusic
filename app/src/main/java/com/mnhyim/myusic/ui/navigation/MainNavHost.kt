package com.mnhyim.myusic.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.mnhyim.myusic.ui.feature.home.Home
import com.mnhyim.myusic.ui.feature.player.Player

@Composable
fun MainNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier
    ) {
        composable<Routes.Home> {
            Home(
                onNavigate = { navController.navigate(Routes.Player) },
            )
        }
        composable<Routes.Player> {
            Player(
                onBackNavigate = { navController.popBackStack() }
            )
        }
    }
}