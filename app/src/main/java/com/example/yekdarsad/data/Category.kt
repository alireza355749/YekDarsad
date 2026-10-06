package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // شناسه مشترک این رکورد در Supabase
    //
    // برای رکوردهای جدیدی که هنوز Sync نشده‌اند = null
    val cloudId: String? = null,

    val name: String,

    val icon: String,

    val deleted: Boolean = false,

    // نسخه‌ای از وضعیت Category که آخرین بار
    // با Supabase با موفقیت Sync شده است.
    //
    // این مقدار برای تشخیص Conflict استفاده می‌شود.
    val syncBase: String? = null,

    // نسخه Supabase که در آخرین Sync دریافت شده است.
    val syncVersion: Long = 0
)