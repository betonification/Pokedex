package com.plcoding.jetpackcomposepokedex.ui.screens.pokemonlist

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.plcoding.jetpackcomposepokedex.data.local.PokemonEntry
import com.plcoding.jetpackcomposepokedex.data.models.PokedexListEntry
import com.plcoding.jetpackcomposepokedex.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.util.Constants.PAGE_SIZE
import com.plcoding.jetpackcomposepokedex.util.Constants.POKEMON_IMAGE_BASE_URL
import com.plcoding.jetpackcomposepokedex.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class PokemonListViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel(){

    private var curPage = 0

    var pokemonList = mutableStateOf<List<PokedexListEntry>>(listOf())
    var loadError = mutableStateOf("")
    var isLoading = mutableStateOf(false)
    var endReached = mutableStateOf(false)


    private var cachedPokemonList = listOf<PokedexListEntry>()
    private var isSearchStarting = true
    var isSearching = mutableStateOf(false)

    fun searchPokemonList(query: String){
        val listToSearch = if (isSearchStarting){
            pokemonList.value
        } else{
            cachedPokemonList
        }
        viewModelScope.launch(Dispatchers.Default) {
            if (query.isEmpty()){
                pokemonList.value = cachedPokemonList
                isSearching.value = false
                isSearchStarting = true
                return@launch
            }
            val results = listToSearch.filter {
                it.pokemonName.contains(query.trim(), ignoreCase = true) ||
                        it.id.toString() == (query.trim())
            }
            if (isSearchStarting){
                cachedPokemonList = pokemonList.value
                isSearchStarting = false
            }
            pokemonList.value = results
            isSearching.value = true
        }
    }


    fun loadPokemonPaginated(){
        viewModelScope.launch {
            isLoading.value = true
            val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
            when(result){
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    for (pokemonEntry in result.data.results){
                        val id = if (pokemonEntry.url.endsWith("/")){
                            pokemonEntry.url.dropLast(1).takeLastWhile {
                                it.isDigit()
                            }
                        }else{
                            pokemonEntry.url.takeLastWhile {
                                it.isDigit()
                            }
                        }
                        val url = POKEMON_IMAGE_BASE_URL + "${id}.png"
                        // dodavanje pokemona u bazu
                        withContext(Dispatchers.IO){
                            val pokemon = PokemonEntry(
                                id = id.toInt(),
                                pokemonName = pokemonEntry.name.replaceFirstChar { it.uppercaseChar() },
                                imageUrl = url
                            )
                            repository.addPokemonToDatabase(pokemon)
                        }
                    }
                    curPage++
                    loadError.value = ""
                    isLoading.value = false
                    withContext(Dispatchers.IO){
                        val pokemonListFromDatabase =
                            repository.getPokemonEntriesFromDatabase().mapIndexed { index, pokemonEntry ->
                                PokedexListEntry(pokemonEntry.pokemonName, pokemonEntry.imageUrl, pokemonEntry.id)
                            }
                        pokemonList.value = pokemonListFromDatabase
                    }
                }
                is Resource.Error -> {
                    viewModelScope.launch(Dispatchers.IO) {
                        val pokemonListFromDatabase =
                            repository.getPokemonEntriesFromDatabase().mapIndexed { index, pokemonEntry ->
                                PokedexListEntry(pokemonEntry.pokemonName, pokemonEntry.imageUrl, pokemonEntry.id)
                            }
                        if (pokemonListFromDatabase.isEmpty()){
                            loadError.value = result.message ?: ""
                        }else{
                            pokemonList.value = pokemonListFromDatabase
                        }
                        isLoading.value = false
                    }
                }
            }
        }
    }

    /*fun loadPokemonPaginated(){
        viewModelScope.launch {
            if (loadedFromDatabase.value){
                pokemonList.value = listOf()
                loadedFromDatabase.value = false
            }
            isLoading.value = true
            val result = repository.getPokemonList(PAGE_SIZE, curPage * PAGE_SIZE)
            when(result){
                is Resource.Success -> {
                    endReached.value = curPage * PAGE_SIZE >= result.data!!.count
                    val pokedexEntries = result.data.results.mapIndexed { index, entry ->
                        val id = if (entry.url.endsWith("/")){
                            entry.url.dropLast(1).takeLastWhile {
                                it.isDigit()
                            }
                        }else{
                            entry.url.takeLastWhile {
                                it.isDigit()
                            }
                        }
                        val url = POKEMON_IMAGE_BASE_URL + "${id}.png"
                        // dodavanje pokemona u bazu
                        withContext(Dispatchers.IO){
                            val pokemon = PokemonEntry(
                                id = id.toInt(),
                                pokemonName = entry.name.replaceFirstChar { it.uppercaseChar() },
                                imageUrl = url
                            )
                            repository.addPokemonToDatabase(pokemon)
                        }
                        PokedexListEntry(entry.name.replaceFirstChar { it.uppercaseChar() }, url, id.toInt())
                    }

                    curPage++
                    loadError.value = ""
                    isLoading.value = false
                    pokemonList.value += pokedexEntries
                }
                is Resource.Error -> {
                    loadError.value = result.message ?: ""
                    isLoading.value = false
                }
            }
        }
    }*/

    fun loadPokemonListFromDatabase(){
        viewModelScope.launch(Dispatchers.IO) {
            val pokemonListFromDatabase =
                repository.getPokemonEntriesFromDatabase().mapIndexed { index, pokemonEntry ->
                    PokedexListEntry(pokemonEntry.pokemonName, pokemonEntry.imageUrl, pokemonEntry.id)
                }
            pokemonList.value = pokemonListFromDatabase
            Log.i("POKEMON_LIST", "${pokemonList.value[99]}")
            curPage = pokemonList.value.size / PAGE_SIZE
        }
    }
}