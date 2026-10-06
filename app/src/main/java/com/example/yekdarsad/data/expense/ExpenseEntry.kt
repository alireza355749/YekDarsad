package com.example.yekdarsad.data.expense

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "expense_entries")
data class ExpenseEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // شناسه یکتای مشترک بین اندروید و وب
    val cloudId: String? = null,

    // تاریخ به صورت yyyy-MM-dd
    val date: String,

    // INCOME یا EXPENSE
    val type: ExpenseType,

    // مبلغ به تومان
    val amount: Long,

    // دسته‌بندی هزینه یا درآمد
    val category: String,

    // عنوان تراکنش
    val title: String,

    // توضیحات اختیاری
    val description: String = "",

    // زمان ثبت
    val createdAt: Long = System.currentTimeMillis(),

    // آخرین زمان ویرایش، برای تشخیص نسخه جدیدتر هنگام سینک
    val updatedAt: Long = System.currentTimeMillis(),

    // زمان حذف؛ null یعنی رکورد حذف نشده است
    val deletedAt: Long? = null
)

enum class ExpenseType {
    INCOME,
    EXPENSE
}