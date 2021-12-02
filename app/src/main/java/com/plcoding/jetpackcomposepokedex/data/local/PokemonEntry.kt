package com.plcoding.jetpackcomposepokedex.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_entry_table")
data class PokemonEntry(
    @PrimaryKey(autoGenerate = false)
    val id: Int,
    val pokemonName: String,
    val imageUrl: String
)