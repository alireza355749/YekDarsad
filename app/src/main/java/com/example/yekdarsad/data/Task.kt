package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // شناسه مشترک این Task در Supabase
    //
    // برای Taskهای جدیدی که هنوز Sync نشده‌اند = null
    val cloudId: String? = null,

    val categoryId: Int,

    val title: String,

    val coefficient: Double,

    // TIME = فعالیت زمانی
    // CHECK = فعالیت تیکی
    val type: String = "TIME",

    // ترتیب نمایش در لیست
    val orderIndex: Int = 0,

    // =========================================
    // کالری ورزش
    // =========================================

    // اگر Task مربوط به ورزش باشد،
    // مقدار کالری مصرف‌شده در یک ساعت
    // اینجا ذخیره می‌شود.
    //
    // برای فعالیت‌های معمولی = 0
    val caloriesPerHour: Double = 0.0,

    // =========================================
    // SYNC
    // =========================================

    // آیا Task به صورت محلی حذف شده است؟
    //
    // حذف فیزیکی برای Sync انجام نمی‌شود،
    // تا Sync بتواند متوجه حذف شدن رکورد شود.
    val deleted: Boolean = false,

    // نسخه‌ای از وضعیت Task که آخرین بار
    // با Supabase با موفقیت Sync شده است.
    //
    // این مقدار برای تشخیص تغییرات محلی و Conflict استفاده می‌شود.
    val syncBase: String? = null,

    // نسخه Supabase که در آخرین Sync دریافت شده است.
    val syncVersion: Long = 0
)