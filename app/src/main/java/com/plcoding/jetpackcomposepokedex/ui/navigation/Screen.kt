package com.plcoding.jetpackcomposepokedex.ui.navigation

sealed class Screen(val route: String) {
    object PokemonList: Screen("pokemon_list_screen")
    object PokemonDetails: Screen("pokemon_detail_screen")

    fun withArgs(vararg args: String): String{
        return buildString {
            append(route)
            args.forEach {
                append("/$it")
            }
        }
    }
}