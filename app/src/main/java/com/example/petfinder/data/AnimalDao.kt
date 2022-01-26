package com.example.petfinder.data

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AnimalDao {

    @Query("SELECT * from animal_table ORDER BY name ASC")
    fun getAlphabetizedAnimals(): LiveData<List<AnimalDto>>

    @Query("SELECT * from animal_table WHERE type=:type AND breed=:breed ORDER BY name ASC")
    fun getAnimalsByTypeAndBreed(type: String, breed: String): LiveData<List<AnimalDto>>

    @Query("SELECT * from animal_table WHERE type=:type ORDER BY name ASC")
    fun getAnimalsByType(type: String): LiveData<List<AnimalDto>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(animals: List<AnimalDto>)

    @Query("DELETE FROM animal_table")
    fun deleteAll()

    @Query("SELECT DISTINCT type from animal_table")
    fun getTypes(): LiveData<List<String>>

    @Query("SELECT DISTINCT breed from animal_table WHERE type=:type")
    fun getBreedsForType(type: String): LiveData<List<String>>
}
