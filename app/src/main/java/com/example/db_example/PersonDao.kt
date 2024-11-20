package com.example.db_example

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface PersonDao {
    @Query("SELECT * FROM persons ORDER BY id DESC")
    fun getAllPersons(): Flow<List<Person>>

    @Insert
    suspend fun insertPerson(person: Person)
}