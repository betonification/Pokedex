package com.plcoding.jetpackcomposepokedex.data.remote

import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responses.PokemonList
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApi {

    //funkcija koja treba da vrati listu pokemona
    //value u zagradi je nastavak nakon baseURL, koristi se da bi dobili JSON bas sa tog linka
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<PokemonList>

    @GET("pokemon/{id}")
    suspend fun getPokemonInfo(
        //menjamo {name} parametar u GET sa name koji prosledimo funkciji
        @Path("id") number: Int
    ): Response<Pokemon>
}