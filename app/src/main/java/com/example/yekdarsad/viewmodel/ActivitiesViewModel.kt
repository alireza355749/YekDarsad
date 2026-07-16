package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.CategoryDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivitiesViewModel(
    private val dao: CategoryDao
) : ViewModel() {


    private val _categories =
        MutableStateFlow<List<Category>>(emptyList())

    val categories: StateFlow<List<Category>> = _categories


    init {
        loadCategories()
    }


    private fun loadCategories() {

        viewModelScope.launch {

            _categories.value = dao.getAll()

        }
    }


    fun addCategory(name: String, icon: String) {

        viewModelScope.launch {

            dao.insert(
                Category(
                    name = name,
                    icon = icon
                )
            )

            loadCategories()

        }
    }

    fun deleteCategory(category: Category) {

        viewModelScope.launch {

            dao.delete(category)

            loadCategories()

        }

    }
}