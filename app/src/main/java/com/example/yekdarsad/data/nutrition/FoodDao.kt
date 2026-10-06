package com.example.yekdarsad.data.nutrition

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface FoodDao {

    @Query("SELECT * FROM foods ORDER BY name")
    suspend fun getAllFoods(): List<Food>

    @Query(
        """
        SELECT * FROM foods
        WHERE name LIKE '%' || :query || '%'
        ORDER BY name
        """
    )
    suspend fun searchFoods(
        query: String
    ): List<Food>

    @Query(
        """
        SELECT * FROM foods
        WHERE id = :id
        LIMIT 1
        """
    )
    suspend fun getFoodById(
        id: Long
    ): Food?

    @Query(
        """
        SELECT * FROM foods
        WHERE name = :name
        LIMIT 1
        """
    )
    suspend fun getFoodByName(
        name: String
    ): Food?

    @Insert(
        onConflict = OnConflictStrategy.REPLACE
    )
    suspend fun insertFoods(
        foods: List<Food>
    )
}