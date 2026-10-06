
package com.example.yekdarsad.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.yekdarsad.data.ActivityDuration
import com.example.yekdarsad.data.ActivityDurationDao
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.data.CategoryDao
import com.example.yekdarsad.data.DailyPlanDao
import com.example.yekdarsad.data.DailyTaskDao
import com.example.yekdarsad.data.Task
import com.example.yekdarsad.data.TaskDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ActivitiesViewModel(
    private val categoryDao: CategoryDao,
    private val taskDao: TaskDao,
    private val durationDao: ActivityDurationDao,
    private val dailyTaskDao: DailyTaskDao,
    private val dailyPlanDao: DailyPlanDao
) : ViewModel() {

    companion object {
        private val PROTECTED_CATEGORY_NAMES = setOf(
            "شغل",
            "ورزش",
            "معنویت",
            "زبان انگلیسی",
            "مهارت‌های شغلی",
            "کتاب و پادکست",
            "تفریح",
            "متفرقه"
        )

        private fun isProtectedCategory(name: String): Boolean {
            return name.trim() in PROTECTED_CATEGORY_NAMES
        }
    }

    private val _categories =
        MutableStateFlow<List<Category>>(emptyList())

    val categories: StateFlow<List<Category>> =
        _categories

    init {
        observeCategories()
    }

    private fun observeCategories() {
        viewModelScope.launch {
            categoryDao.observeAll().collect { categories ->
                _categories.value = categories
            }
        }
    }

    /*
     * ADD CATEGORY
     */

    fun addCategory(
        name: String,
        icon: String,
        onCreated: (Category) -> Unit = {}
    ) {
        viewModelScope.launch {
            val id = categoryDao.insert(
                Category(
                    name = name,
                    icon = icon
                )
            )

            val category = Category(
                id = id.toInt(),
                name = name,
                icon = icon
            )

            createDefaultTasks(category)
            onCreated(category)
        }
    }

    /*
     * DEFAULT TASKS
     */

    private suspend fun createDefaultTasks(
        category: Category
    ) {
        val defaultTasks = when (category.name) {
            "زبان" -> listOf(
                "Reading",
                "Listening",
                "Speaking",
                "Writing",
                "Vocabulary"
            )

            "مطالعه" -> listOf(
                "مطالعه کتاب",
                "مطالعه مقاله",
                "یادداشت‌برداری"
            )

            "ورزش" -> listOf(
                "تمرین قدرتی",
                "کاردیو",
                "کشش"
            )

            "مهارت‌های شغلی" -> listOf(
                "یادگیری مهارت",
                "تمرین عملی",
                "پروژه"
            )

            "شغل" -> listOf(
                "کار اصلی",
                "جلسه",
                "کار اداری"
            )

            else -> emptyList()
        }

        if (defaultTasks.isEmpty()) return

        defaultTasks.forEachIndexed { index, title ->
            val taskId = taskDao.insert(
                Task(
                    categoryId = category.id,
                    title = title,
                    coefficient = 1.0,
                    type = "TIME",
                    orderIndex = index
                )
            )

            listOf(30, 60, 90, 120).forEach { minutes ->
                durationDao.insert(
                    ActivityDuration(
                        taskId = taskId.toInt(),
                        minutes = minutes
                    )
                )
            }
        }
    }

    /*
     * DELETE CATEGORY
     */

    fun deleteCategory(category: Category) {
        // دسته‌های پیش‌فرض هرگز نباید حذف شوند.
        if (isProtectedCategory(category.name)) {
            return
        }

        viewModelScope.launch {
            try {
                // دوباره از دیتابیس بررسی می‌کنیم تا از حذف
                // دسته محافظت‌شده با یک فراخوانی قدیمی جلوگیری شود.
                val currentCategory =
                    categoryDao.getAllIncludingDeleted()
                        .firstOrNull { it.id == category.id }

                if (currentCategory == null) return@launch

                if (isProtectedCategory(currentCategory.name)) {
                    return@launch
                }

                if (currentCategory.deleted) {
                    return@launch
                }

                val tasks = taskDao.getTasksByCategoryOnce(
                    category.id
                )

                tasks.forEach { task ->
                    // DailyTask فقط داده محلی Today است.
                    dailyTaskDao.deleteByTaskId(task.id)

                    // DailyPlan باید برای سینک، tombstone داشته باشد.
                    dailyPlanDao.markDeleted(task.id)

                    // Duration داده محلی فعالیت است.
                    durationDao.deleteByTaskId(task.id)

                    // Task جزو Sync است.
                    taskDao.markDeleted(task.id)
                }

                // فقط دسته‌های قابل‌حذف به این مرحله می‌رسند.
                categoryDao.markDeleted(category.id)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}