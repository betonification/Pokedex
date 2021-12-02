package com.plcoding.jetpackcomposepokedex.data.local

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [PokemonEntry::class], version = 1, exportSchema = false)
abstract class PokemonEntryDatabase: RoomDatabase() {

    abstract fun pokemonEntryDao(): PokemonEntryDao
}