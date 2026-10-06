package com.example.yekdarsad.data.nutrition

import androidx.room.TypeConverter

class NutritionConverters {


    @TypeConverter
    fun fromMealType(
        value: MealType
    ): String {

        return value.name

    }


    @TypeConverter
    fun toMealType(
        value: String
    ): MealType {

        return MealType.valueOf(value)

    }

}