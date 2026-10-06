package com.example.yekdarsad.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_plans")
data class DailyPlan(

    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    // شناسه Task محلی
    val taskId: Int,

    // شناسه Routine سازنده این DailyPlan
    //
    // برای برنامه‌های عادی = null
    // برای برنامه‌هایی که توسط Routine ساخته شده‌اند = شناسه همان Routine
    val routineId: Int? = null,

    val date: String,

    val plannedMinutes: Int,

    val actualMinutes: Int = 0,

    val earnedScore: Double = 0.0,

    val completed: Boolean = false,

    // کالری سوزانده‌شده توسط این فعالیت
    //
    // مقدار بر اساس actualMinutes محاسبه می‌شود.
    // برای فعالیت‌های غیرورزشی = 0
    val exerciseCalories: Double = 0.0,

    // ثبت نهایی روز برای ورود به آمار
    val dayRegistered: Boolean = false,

    // ترتیب نمایش کارت‌ها در Today
    val orderIndex: Int = 0,

    // توضیحات مخصوص همین فعالیت در همین روز
    val note: String = "",

    // ==========================================
    // SYNC
    // ==========================================

    // شناسه مشترک این DailyPlan در Supabase
    //
    // برای رکوردی که هنوز Sync نشده = null
    val cloudId: String? = null,

    // حذف نرم محلی
    //
    // رکورد حذف‌شده تا زمان مشخص شدن وضعیت Sync
    // از دیتابیس فیزیکی حذف نمی‌شود.
    val deleted: Boolean = false,

    // آخرین وضعیت این رکورد که بین Android و Cloud
    // مشترک بوده است.
    //
    // برای تشخیص تغییرات محلی و Conflict استفاده می‌شود.
    val syncBase: String? = null,

    // آخرین version دریافتی از Supabase
    val syncVersion: Long = 0
)