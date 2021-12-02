package com.plcoding.jetpackcomposepokedex.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface PokemonEntryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addPokemonToDatabase(pokemonEntry: PokemonEntry)

    @Query("SELECT * FROM pokemon_entry_table ORDER BY id ASC")
    fun getPokemonEntriesFromDatabase(): List<PokemonEntry>
}