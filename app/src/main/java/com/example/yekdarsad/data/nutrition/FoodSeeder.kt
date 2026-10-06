package com.example.yekdarsad.data.nutrition

import android.content.Context
import org.json.JSONArray

class FoodSeeder(
    private val context: Context,
    private val foodDao: FoodDao
) {

    suspend fun seed() {

        val jsonText =
            context.assets
                .open("foods.json")
                .bufferedReader()
                .use {
                    it.readText()
                }

        val jsonArray =
            JSONArray(jsonText)

        val existingFoods =
            foodDao.getAllFoods()

        val existingByName =
            existingFoods.associateBy {
                it.name.trim()
            }

        val foodsByName =
            linkedMapOf<String, Food>()

        for (i in 0 until jsonArray.length()) {

            val item =
                jsonArray.getJSONObject(i)

            val name =
                item.getString("name").trim()

            if (name.isBlank()) {
                continue
            }

            val existingFood =
                existingByName[name]

            val servingUnit =
                if (
                    item.has("servingUnit") &&
                    !item.isNull("servingUnit")
                ) {
                    item.getString("servingUnit").trim()
                } else {
                    "گرم"
                }

            val servingAmount =
                if (
                    item.has("servingAmount") &&
                    !item.isNull("servingAmount")
                ) {
                    item.getDouble("servingAmount")
                } else {
                    100.0
                }

            val servingWeightGrams =
                if (
                    item.has("servingWeightGrams") &&
                    !item.isNull("servingWeightGrams")
                ) {
                    item.getDouble("servingWeightGrams")
                } else {
                    100.0
                }

            val food =
                Food(

                    id =
                        existingFood?.id
                            ?: 0L,

                    name =
                        name,

                    category =
                        item.getString("category"),

                    caloriesPer100g =
                        item.getDouble("caloriesPer100g"),

                    protein =
                        item.getDouble("protein"),

                    carbs =
                        item.getDouble("carbs"),

                    fat =
                        item.getDouble("fat"),

                    fiber =
                        item.getDouble("fiber"),

                    vitaminA =
                        item.getDouble("vitaminA"),

                    vitaminB =
                        item.getDouble("vitaminB"),

                    vitaminC =
                        item.getDouble("vitaminC"),

                    vitaminD =
                        item.getDouble("vitaminD"),

                    calcium =
                        item.getDouble("calcium"),

                    iron =
                        item.getDouble("iron"),

                    servingUnit =
                        servingUnit,

                    servingAmount =
                        servingAmount,

                    servingWeightGrams =
                        servingWeightGrams
                )

            /*
             * اگر یک غذا چند بار داخل JSON آمده باشد،
             * آخرین نسخه آن نگه داشته می‌شود.
             */
            foodsByName[name] =
                food
        }

        val foods =
            foodsByName.values.toList()

        if (foods.isNotEmpty()) {

            foodDao.insertFoods(
                foods
            )
        }
    }
}