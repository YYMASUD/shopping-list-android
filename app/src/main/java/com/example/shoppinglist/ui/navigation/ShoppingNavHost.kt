package com.example.shoppinglist.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.shoppinglist.ui.additem.AddEditItemScreen
import com.example.shoppinglist.ui.list.ShoppingListScreen

/**
 * Root navigation graph. Two destinations:
 *  - [Routes.LIST]: the shopping list.
 *  - [Routes.ADD_EDIT]: the add/edit form (optional itemId argument).
 *
 * Horizontal slide transitions give the add/edit flow a smooth, native feel.
 */
@Composable
fun ShoppingNavHost() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.LIST
    ) {
        composable(
            route = Routes.LIST,
            exitTransition = { slideOutHorizontally(tween(300)) { -it / 4 } + fadeOut(tween(300)) },
            popEnterTransition = { slideInHorizontally(tween(300)) { -it / 4 } + fadeIn(tween(300)) }
        ) {
            ShoppingListScreen(
                onAddItem = { navController.navigate(Routes.addItem()) },
                onEditItem = { id -> navController.navigate(Routes.editItem(id)) }
            )
        }

        composable(
            route = Routes.ADD_EDIT,
            arguments = listOf(
                navArgument(Routes.ITEM_ID_ARG) {
                    type = NavType.LongType
                    defaultValue = Routes.NEW_ITEM_ID
                }
            ),
            enterTransition = { slideInHorizontally(tween(300)) { it } + fadeIn(tween(300)) },
            popExitTransition = { slideOutHorizontally(tween(300)) { it } + fadeOut(tween(300)) }
        ) {
            AddEditItemScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

