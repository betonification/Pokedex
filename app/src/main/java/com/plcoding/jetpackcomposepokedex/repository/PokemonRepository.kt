package com.plcoding.jetpackcomposepokedex.repository

import com.plcoding.jetpackcomposepokedex.data.local.PokemonEntry
import com.plcoding.jetpackcomposepokedex.data.local.PokemonEntryDao
import com.plcoding.jetpackcomposepokedex.data.remote.PokeApi
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.data.remote.responses.PokemonList
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.scopes.ActivityScoped
import javax.inject.Inject

@ActivityScoped
class PokemonRepository @Inject constructor(
    private val api: PokeApi,
    private val pokemonEntryDao: PokemonEntryDao
){
    suspend fun getPokemonList(limit: Int, offset: Int): Resource<PokemonList>{
        val message = "something went wrong"
        val response = try{
            api.getPokemonList(limit, offset)
        }catch (e: Exception){
            return Resource.Error("Something went wrong")
        }
        return Resource.Success(response.body() ?: return Resource.Error(message))
    }

    suspend fun getPokemonInfo(number: Int): Resource<Pokemon>{
        val message = "something went wrong"
        val response = try{
            api.getPokemonInfo(number)
        }catch (e: Exception){
            return Resource.Error(message)
        }
        return Resource.Success(response.body() ?: return Resource.Error(message))
    }

    suspend fun addPokemonToDatabase(pokemonEntry: PokemonEntry) = pokemonEntryDao.addPokemonToDatabase(pokemonEntry)

    suspend fun getPokemonEntriesFromDatabase() = pokemonEntryDao.getPokemonEntriesFromDatabase()
}