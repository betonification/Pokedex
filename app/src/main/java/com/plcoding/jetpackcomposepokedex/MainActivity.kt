package com.plcoding.jetpackcomposepokedex

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.plcoding.jetpackcomposepokedex.ui.navigation.Screen
import com.plcoding.jetpackcomposepokedex.ui.screens.pokemondetail.PokemonDetailScreen
import com.plcoding.jetpackcomposepokedex.ui.screens.pokemonlist.PokemonListScreen
import com.plcoding.jetpackcomposepokedex.ui.theme.JetpackComposePokedexTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            JetpackComposePokedexTheme {
                PokemonNavHost()
            }
        }
    }
}

@Composable
fun PokemonNavHost() {
    val navController = rememberNavController()
    NavHost(
        navController = navController,
        startDestination = Screen.PokemonList.route
    ){
        composable(
            route = Screen.PokemonList.route
        ){
            PokemonListScreen(navController = navController)
        }
        composable(
            route = Screen.PokemonDetails.route + "/{id}",
            arguments = listOf(
                navArgument("id"){
                    type = NavType.IntType
                    defaultValue = 1
                }
            )
        ){entry->
            val id = remember{
                entry.arguments?.getInt("id")
            }
            PokemonDetailScreen(
                id = id ?: 1,
                navController = navController)
        }
    }
}