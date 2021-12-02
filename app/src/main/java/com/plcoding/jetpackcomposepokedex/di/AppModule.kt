package com.plcoding.jetpackcomposepokedex.di

import android.content.Context
import androidx.room.Room
import com.plcoding.jetpackcomposepokedex.data.local.PokemonEntryDao
import com.plcoding.jetpackcomposepokedex.data.local.PokemonEntryDatabase
import com.plcoding.jetpackcomposepokedex.data.remote.PokeApi
import com.plcoding.jetpackcomposepokedex.repository.PokemonRepository
import com.plcoding.jetpackcomposepokedex.util.Constants.BASE_URL
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Singleton
    @Provides
    fun providePokemonRepository(
        api: PokeApi,
        dao: PokemonEntryDao
    ) = PokemonRepository(api, dao)

    @Singleton
    @Provides
    fun providePokeApi(): PokeApi{
        /*val interceptor = HttpLoggingInterceptor()
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY)
        val client = OkHttpClient.Builder().addInterceptor(interceptor).build()*/

        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(BASE_URL)
            //.client(client)
            .build()
            .create(PokeApi::class.java)
    }

    @Singleton
    @Provides
    fun providePokemonEntryDatabase(
        @ApplicationContext app:Context
    ) = Room.databaseBuilder(
        app,
        PokemonEntryDatabase::class.java,
        "pokemon_entry_database"
    ).build()

    @Singleton
    @Provides
    fun providePokemonEntryDao(db: PokemonEntryDatabase) = db.pokemonEntryDao()
}