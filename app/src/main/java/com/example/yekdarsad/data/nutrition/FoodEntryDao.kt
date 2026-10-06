package com.example.yekdarsad.data.nutrition

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query


@Dao
interface FoodEntryDao {


    @Insert
    suspend fun insert(
        entry: FoodEntry
    )



    @Query(
        """
        SELECT * FROM food_entries
        WHERE date = :date
        ORDER BY createdAt DESC
        """
    )
    suspend fun getByDate(
        date: String
    ): List<FoodEntry>



    @Query(
        """
        DELETE FROM food_entries
        WHERE id = :id
        """
    )
    suspend fun delete(
        id: Long
    )



    @Query(
        """
        DELETE FROM food_entries
        WHERE date = :date
        """
    )
    suspend fun deleteByDate(
        date: String
    )

}