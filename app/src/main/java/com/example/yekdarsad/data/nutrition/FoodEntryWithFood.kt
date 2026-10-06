package com.example.yekdarsad.data.nutrition

import androidx.room.Embedded
import androidx.room.Relation


data class FoodEntryWithFood(

    @Embedded
    val entry: FoodEntry,


    @Relation(
        parentColumn = "foodId",
        entityColumn = "id"
    )
    val food: Food?

)
