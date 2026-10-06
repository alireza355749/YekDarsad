package com.example.yekdarsad.ui.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.yekdarsad.data.DatabaseProvider
import com.example.yekdarsad.data.expense.ExpenseEntry
import com.example.yekdarsad.data.expense.ExpenseRepository
import com.example.yekdarsad.data.expense.ExpenseType
import com.example.yekdarsad.viewmodel.ExpenseViewModel
import com.example.yekdarsad.viewmodel.ExpenseViewModelFactory
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.foundation.text.KeyboardOptions

private val expenseCategories = listOf(
    "خوراک",
    "پوشاک",
    "کتاب",
    "یادگیری‌های شغلی",
    "قرض",
    "خودرو",
    "استایل",
    "درمانی",
    "هدیه",
    "رابطه",
    "خانه",
    "سفر",
    "تفریح",
    "قبض و خدمات",
    "موبایل و اینترنت",
    "لوازم شخصی",
    "سایر"
)

private val incomeCategories = listOf(
    "شغل اول",
    "شغل دوم",
    "شغل سوم"
)

private val Purple = Color(0xFF6C63FF)
private val PurpleLight = Color(0xFFEDE7FF)
private val PurpleBackground = Color(0xFFF8F6FF)

private val Green = Color(0xFF43A047)
private val Red = Color(0xFFE53935)

@Composable
fun ExpensesScreen(
    date: String,
    onBack: () -> Unit
) {

    val context = LocalContext.current

    val database = remember {
        DatabaseProvider.getDatabase(context)
    }

    val repository = remember {
        ExpenseRepository(
            database.expenseDao()
        )
    }

    val expenseViewModel: ExpenseViewModel = viewModel(
        factory = ExpenseViewModelFactory(repository)
    )

    val entries by expenseViewModel
        .getEntriesForDate(date)
        .collectAsState(initial = emptyList())

    val totalExpenses by expenseViewModel
        .getTotalExpensesForDate(date)
        .collectAsState(initial = 0L)

    val totalIncome by expenseViewModel
        .getTotalIncomeForDate(date)
        .collectAsState(initial = 0L)

    var showAddDialog by remember {
        mutableStateOf(false)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleBackground)
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = 8.dp,
                    end = 16.dp,
                    top = 12.dp,
                    bottom = 8.dp
                ),
            verticalAlignment = Alignment.CenterVertically
        ) {

            IconButton(
                onClick = onBack
            ) {

                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "بازگشت"
                )
            }

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Text(
                    text = "💰 مخارج و درآمد",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = date,
                    color = Color.Gray
                )
            }
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),

            verticalArrangement =
                Arrangement.spacedBy(14.dp)
        ) {

            item {

                Spacer(
                    modifier = Modifier.height(2.dp)
                )
            }

            // =================================================
            // خلاصه درآمد و هزینه
            // =================================================

            item {

                SummaryCard(
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses
                )
            }

            // =================================================
            // عنوان تراکنش‌ها
            // =================================================

            item {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.SpaceBetween,
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = "تراکنش‌های امروز",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "${entries.size} مورد",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                }
            }

            // =================================================
            // لیست تراکنش‌ها
            // =================================================

            if (entries.isEmpty()) {

                item {

                    EmptyExpenseState()
                }

            } else {

                items(
                    items = entries,
                    key = {
                        it.id
                    }
                ) { entry ->

                    ExpenseEntryCard(
                        entry = entry,
                        onDelete = {
                            expenseViewModel.deleteEntry(entry)
                        }
                    )
                }
            }

            item {

                Spacer(
                    modifier = Modifier.height(90.dp)
                )
            }
        }
    }

    // =================================================
    // Floating Button
    // =================================================

    Box(
        modifier = Modifier.fillMaxSize()
    ) {

        FloatingActionButton(
            onClick = {
                showAddDialog = true
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(20.dp),
            containerColor = Purple,
            contentColor = Color.White
        ) {

            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = "ثبت تراکنش"
            )
        }
    }

    // =================================================
    // Add Dialog
    // =================================================

    if (showAddDialog) {

        AddExpenseDialog(

            onDismiss = {
                showAddDialog = false
            },

            onAddExpense = {
                    amount,
                    category,
                    title,
                    description ->

                expenseViewModel.addExpense(
                    date = date,
                    amount = amount,
                    category = category,
                    title = title,
                    description = description
                )

                showAddDialog = false
            },

            onAddIncome = {
                    amount,
                    category,
                    title,
                    description ->

                expenseViewModel.addIncome(
                    date = date,
                    amount = amount,
                    category = category,
                    title = title,
                    description = description
                )

                showAddDialog = false
            }
        )
    }
}

// =====================================================
// Summary Card
// =====================================================

@Composable
private fun SummaryCard(
    totalIncome: Long,
    totalExpenses: Long
) {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = PurpleLight
        )
    ) {

        Column(
            modifier = Modifier.padding(18.dp)
        ) {

            Text(
                text = "وضعیت مالی امروز",
                fontWeight = FontWeight.Bold
            )

            Spacer(
                modifier = Modifier.height(16.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement =
                    Arrangement.SpaceBetween
            ) {

                MoneyStat(
                    title = "درآمد",
                    value = totalIncome,
                    icon = Icons.Default.TrendingUp,
                    color = Green
                )

                MoneyStat(
                    title = "هزینه",
                    value = totalExpenses,
                    icon = Icons.Default.TrendingDown,
                    color = Red
                )
            }

            Spacer(
                modifier = Modifier.height(18.dp)
            )

            HorizontalDivider(
                color = Color(0xFFD8D1EA)
            )

            Spacer(
                modifier = Modifier.height(12.dp)
            )

            Text(
                text = "درآمد و هزینه به صورت جداگانه ثبت می‌شوند.",
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// =====================================================
// Money Stat
// =====================================================

@Composable
private fun MoneyStat(
    title: String,
    value: Long,
    icon: ImageVector,
    color: Color
) {

    Column(
        modifier = Modifier.width(145.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = color,
            modifier = Modifier.size(22.dp)
        )

        Spacer(
            modifier = Modifier.height(5.dp)
        )

        Text(
            text = title,
            style = MaterialTheme.typography.labelMedium,
            color = Color.Gray
        )

        Spacer(
            modifier = Modifier.height(3.dp)
        )

        Text(
            text = formatMoney(value),
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.titleMedium,
            color = color
        )
    }
}

// =====================================================
// Expense Entry Card
// =====================================================

@Composable
private fun ExpenseEntryCard(
    entry: ExpenseEntry,
    onDelete: () -> Unit
) {

    val isIncome =
        entry.type == ExpenseType.INCOME

    val icon =
        if (isIncome) {
            Icons.Default.TrendingUp
        } else {
            Icons.Default.TrendingDown
        }

    val iconColor =
        if (isIncome) {
            Green
        } else {
            Red
        }

    val typeText =
        if (isIncome) {
            "درآمد"
        } else {
            "هزینه"
        }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),

            verticalAlignment =
                Alignment.CenterVertically
        ) {

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .background(
                        color = iconColor.copy(alpha = 0.10f),
                        shape = RoundedCornerShape(12.dp)
                    ),

                contentAlignment = Alignment.Center
            ) {

                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = iconColor
                )
            }

            Spacer(
                modifier = Modifier.width(12.dp)
            )

            Column(
                modifier = Modifier.weight(1f)
            ) {

                Row(
                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    Text(
                        text = entry.title,
                        fontWeight = FontWeight.SemiBold
                    )

                    Spacer(
                        modifier = Modifier.width(6.dp)
                    )

                    Box(
                        modifier = Modifier
                            .background(
                                color = iconColor.copy(
                                    alpha = 0.10f
                                ),
                                shape = RoundedCornerShape(8.dp)
                            )
                            .padding(
                                horizontal = 7.dp,
                                vertical = 3.dp
                            )
                    ) {

                        Text(
                            text = typeText,
                            style =
                                MaterialTheme.typography.labelSmall,
                            color = iconColor,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(3.dp)
                )

                Text(
                    text = entry.category,
                    style =
                        MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )

                if (entry.description.isNotBlank()) {

                    Spacer(
                        modifier = Modifier.height(2.dp)
                    )

                    Text(
                        text = entry.description,
                        style =
                            MaterialTheme.typography.labelSmall,
                        color = Color.Gray
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {

                Text(
                    text = formatMoney(entry.amount),
                    fontWeight = FontWeight.Bold,
                    color = iconColor
                )

                Spacer(
                    modifier = Modifier.height(2.dp)
                )

                TextButton(
                    onClick = onDelete
                ) {

                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "حذف",
                        modifier = Modifier.size(18.dp),
                        tint = Color.Gray
                    )
                }
            }
        }
    }
}

// =====================================================
// Empty State
// =====================================================

@Composable
private fun EmptyExpenseState() {

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 36.dp),

            horizontalAlignment =
                Alignment.CenterHorizontally
        ) {

            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                modifier = Modifier.size(34.dp),
                tint = Purple
            )

            Spacer(
                modifier = Modifier.height(10.dp)
            )

            Text(
                text = "امروز تراکنشی ثبت نشده",
                fontWeight = FontWeight.Medium
            )

            Spacer(
                modifier = Modifier.height(4.dp)
            )

            Text(
                text = "برای ثبت اولین تراکنش روی + بزن",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray
            )
        }
    }
}

// =====================================================
// Add Expense Dialog
// =====================================================

@Composable
private fun AddExpenseDialog(
    onDismiss: () -> Unit,

    onAddExpense: (
        Long,
        String,
        String,
        String
    ) -> Unit,

    onAddIncome: (
        Long,
        String,
        String,
        String
    ) -> Unit
) {

    var isIncome by remember {
        mutableStateOf(false)
    }

    var amountText by remember {
        mutableStateOf("")
    }

    var title by remember {
        mutableStateOf("")
    }

    var category by remember {
        mutableStateOf("")
    }

    var description by remember {
        mutableStateOf("")
    }

    var categoryExpanded by remember {
        mutableStateOf(false)
    }

    val categories =
        if (isIncome) {
            incomeCategories
        } else {
            expenseCategories
        }

    AlertDialog(

        onDismissRequest = onDismiss,

        title = {

            Column {

                Text(
                    text =
                        if (isIncome)
                            "ثبت درآمد جدید"
                        else
                            "ثبت هزینه جدید",

                    fontWeight = FontWeight.Bold
                )

                Spacer(
                    modifier = Modifier.height(4.dp)
                )

                Text(
                    text =
                        if (isIncome)
                            "این مبلغ به درآمدهای شما اضافه می‌شود."
                        else
                            "این مبلغ فقط به عنوان هزینه ثبت می‌شود.",

                    style =
                        MaterialTheme.typography.bodySmall,

                    color = Color.Gray
                )
            }
        },

        text = {

            Column(
                verticalArrangement =
                    Arrangement.spacedBy(10.dp)
            ) {

                // =================================================
                // انتخاب نوع
                // =================================================

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Button(
                        modifier = Modifier.weight(1f),

                        onClick = {
                            isIncome = false
                            category = ""
                            categoryExpanded = false
                        },

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (!isIncome)
                                        Red
                                    else
                                        Color.Transparent,

                                contentColor =
                                    if (!isIncome)
                                        Color.White
                                    else
                                        Red
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.TrendingDown,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        Text("هزینه")
                    }

                    Button(
                        modifier = Modifier.weight(1f),

                        onClick = {
                            isIncome = true
                            category = ""
                            categoryExpanded = false
                        },

                        colors =
                            ButtonDefaults.buttonColors(
                                containerColor =
                                    if (isIncome)
                                        Green
                                    else
                                        Color.Transparent,

                                contentColor =
                                    if (isIncome)
                                        Color.White
                                    else
                                        Green
                            )
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.TrendingUp,
                            contentDescription = null,
                            modifier = Modifier.size(18.dp)
                        )

                        Spacer(
                            modifier = Modifier.width(5.dp)
                        )

                        Text("درآمد")
                    }
                }

                // =================================================
                // مبلغ
                // =================================================

                OutlinedTextField(
                    value = amountText,

                    onValueChange = { input ->

                        val digits = input.filter {
                            it.isDigit()
                        }

                        amountText = formatInputMoney(digits)
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            if (isIncome)
                                "مبلغ درآمد"
                            else
                                "مبلغ هزینه"
                        )
                    },

                    placeholder = {
                        Text("مثلاً 200,000,000")
                    },

                    suffix = {
                        Text("تومان")
                    },

                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),

                    singleLine = true
                )

                // =================================================
                // عنوان
                // =================================================

                OutlinedTextField(

                    value = title,

                    onValueChange = {
                        title = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text(
                            if (isIncome)
                                "عنوان درآمد"
                            else
                                "عنوان هزینه"
                        )
                    },

                    singleLine = true
                )

                // =================================================
                // دسته‌بندی
                // =================================================

                Box(
                    modifier = Modifier.fillMaxWidth()
                ) {

                    OutlinedButton(
                        onClick = {
                            categoryExpanded = true
                        },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement =
                                Arrangement.SpaceBetween,
                            verticalAlignment =
                                Alignment.CenterVertically
                        ) {

                            Text(
                                text =
                                    if (category.isBlank()) {

                                        if (isIncome)
                                            "انتخاب نوع درآمد"
                                        else
                                            "انتخاب دسته‌بندی هزینه"

                                    } else {

                                        category
                                    }
                            )

                            Icon(
                                imageVector =
                                    Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        }
                    }

                    DropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = {
                            categoryExpanded = false
                        }
                    ) {

                        categories.forEach { item ->

                            DropdownMenuItem(

                                text = {
                                    Text(item)
                                },

                                onClick = {

                                    category = item
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // =================================================
                // توضیحات
                // =================================================

                OutlinedTextField(

                    value = description,

                    onValueChange = {
                        description = it
                    },

                    modifier = Modifier.fillMaxWidth(),

                    label = {
                        Text("توضیحات")
                    },

                    maxLines = 2
                )
            }
        },

        confirmButton = {

            Button(

                enabled =
                    amountText
                        .replace(",", "")
                        .toLongOrNull() != null &&
                            title.isNotBlank() &&
                            category.isNotBlank(),

                onClick = {

                    val amount =
                        amountText
                            .replace(",", "")
                            .toLongOrNull()
                            ?: return@Button

                    if (isIncome) {

                        onAddIncome(
                            amount,
                            category,
                            title.trim(),
                            description.trim()
                        )

                    } else {

                        onAddExpense(
                            amount,
                            category,
                            title.trim(),
                            description.trim()
                        )
                    }
                }
            ) {

                Text("ثبت")
            }
        },

        dismissButton = {

            TextButton(
                onClick = onDismiss
            ) {

                Text("انصراف")
            }
        }
    )
}

// =====================================================
// فرمت مبلغ هنگام تایپ
// =====================================================

private fun formatInputMoney(
    digits: String
): String {

    if (digits.isBlank()) {
        return ""
    }

    return digits
        .trimStart('0')
        .ifBlank {
            "0"
        }
        .reversed()
        .chunked(3)
        .joinToString(",")
        .reversed()
}

// =====================================================
// فرمت مبلغ نمایش داده شده
// =====================================================

private fun formatMoney(
    amount: Long
): String {

    val sign =
        if (amount < 0) "-" else ""

    val absolute =
        kotlin.math.abs(amount)

    return sign +
            "%,d".format(
                java.util.Locale.US,
                absolute
            ) +
            " تومان"
}