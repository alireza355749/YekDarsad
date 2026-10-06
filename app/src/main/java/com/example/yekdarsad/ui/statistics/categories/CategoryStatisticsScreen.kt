package com.example.yekdarsad.ui.statistics.categories

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.DatabaseProvider

@Composable
fun CategoryStatisticsScreen(
    onCategorySelected: (Int, String) -> Unit = { _, _ -> }
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    var categories by remember {
        mutableStateOf<List<Category>>(emptyList())
    }

    LaunchedEffect(Unit) {

        categories = database
            .categoryDao()
            .getAll()
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),

        contentPadding = PaddingValues(
            horizontal = 20.dp,
            vertical = 8.dp
        ),

        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {

        items(
            items = categories,
            key = { it.id }
        ) { category ->

            CategoryStatisticsItem(
                category = category,
                onClick = {

                    onCategorySelected(
                        category.id,
                        category.name
                    )
                }
            )
        }
    }
}