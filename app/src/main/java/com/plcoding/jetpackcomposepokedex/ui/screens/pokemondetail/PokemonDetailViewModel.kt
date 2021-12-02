package com.plcoding.jetpackcomposepokedex.ui.screens.pokemondetail

import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.palette.graphics.Palette
import com.plcoding.jetpackcomposepokedex.data.remote.responses.Pokemon
import com.plcoding.jetpackcomposepokedex.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.util.Resource
import com.plcoding.jetpackcomposepokedex.util.Constants.POKEMON_IMAGE_BASE_URL
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PokemonDetailViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _pokemonInfo: MutableLiveData<Resource<Pokemon>?> = MutableLiveData(null)

    val pokemonInfo: LiveData<Resource<Pokemon>?> = _pokemonInfo

    private fun updatePokemonInfo(value: Resource<Pokemon>){
        viewModelScope.launch(Dispatchers.Main) {
            _pokemonInfo.postValue(value)
        }
    }

    fun getPokemonInfo(number: Int){
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getPokemonInfo(number)
            updatePokemonInfo(result)
        }
    }

    fun getPokemonImageUrl(id: Int) : String{
        return POKEMON_IMAGE_BASE_URL + "${id}.png"
    }
}