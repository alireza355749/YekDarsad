package com.example.yekdarsad

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.EventRepeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.Category
import com.example.yekdarsad.ui.components.CategoryVisualCard
import com.example.yekdarsad.ui.components.PlanningBasket
import com.example.yekdarsad.ui.theme.TextPrimary
import com.example.yekdarsad.viewmodel.PlanningViewModel
import java.time.DayOfWeek
import java.time.LocalDate


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ActivitiesContent(
    categories: List<Category>,
    selectedDate: LocalDate,
    planningViewModel: PlanningViewModel,

    // اجازه برنامه‌ریزی برای روزهای گذشته
    allowPastPlanning: Boolean,

    onCategoryClick: (Category) -> Unit,
    onCategoryLongClick: (Category) -> Unit,
    onDateClick: () -> Unit,

    onAddCategory: (String, String) -> Unit,
    onOtherCategoryClick: () -> Unit,

    // ورود به صفحه برنامه‌ریزی روتین
    onRoutinePlanningClick: () -> Unit
) {

    /*
     * =====================================================
     * مجموع زمان برنامه‌ریزی‌شده
     * =====================================================
     */

    val totalMinutes by planningViewModel
        .getTotalMinutes(
            selectedDate.toString()
        )
        .collectAsState(
            initial = 0
        )


    /*
     * =====================================================
     * وضعیت کشوی روتین
     *
     * false:
     * کشو بسته است
     *
     * true:
     * کشو کامل داخل صفحه آمده
     * =====================================================
     */

    var routineDrawerExpanded by remember {
        mutableStateOf(false)
    }


    /*
     * =====================================================
     * دسته‌های اصلی
     * =====================================================
     */

    val mainCategoryNames = listOf(
        "شغل",
        "ورزش",
        "معنویت",
        "زبان انگلیسی",
        "مهارت‌های شغلی",
        "کتاب و پادکست",
        "تفریح"
    )


    /*
     * =====================================================
     * دسته‌های سفارشی
     * =====================================================
     */

    val customCategories =
        categories.filter { category ->

            category.name !in mainCategoryNames &&
                    category.name != "متفرقه"
        }


    /*
     * =====================================================
     * متفرقه
     * =====================================================
     */

    val otherCategory =
        categories.firstOrNull {
            it.name == "متفرقه"
        }


    /*
     * =====================================================
     * آیتم‌های Grid
     * =====================================================
     */

    val gridItems =
        buildList {

            mainCategoryNames.forEach { name ->

                val existingCategory =
                    categories.firstOrNull {
                        it.name == name
                    }

                if (existingCategory != null) {

                    add(
                        ActivityGridItem.RealCategory(
                            existingCategory
                        )
                    )

                } else {

                    add(
                        ActivityGridItem.DefaultCategory(
                            name
                        )
                    )
                }
            }


            customCategories.forEach { category ->

                add(
                    ActivityGridItem.RealCategory(
                        category
                    )
                )
            }


            if (otherCategory != null) {

                add(
                    ActivityGridItem.RealCategory(
                        otherCategory
                    )
                )

            } else {

                add(
                    ActivityGridItem.DefaultCategory(
                        "متفرقه"
                    )
                )
            }


            add(
                ActivityGridItem.NewCategory
            )
        }


    /*
     * =====================================================
     * اطلاعات تاریخ
     * =====================================================
     */

    val today = LocalDate.now()


    /*
     * =====================================================
     * امکان رفتن به روز قبل
     * =====================================================
     */

    val canGoToPreviousDate =
        if (allowPastPlanning) {
            true
        } else {
            selectedDate.isAfter(today)
        }


    /*
     * =====================================================
     * عنوان نسبی روز
     * =====================================================
     */

    val relativeDayText =
        when (selectedDate) {

            today.minusDays(2) ->
                "پریروز"

            today.minusDays(1) ->
                "دیروز"

            today ->
                "امروز"

            today.plusDays(1) ->
                "فردا"

            today.plusDays(2) ->
                "پس‌فردا"

            else ->
                ""
        }


    /*
     * =====================================================
     * روز هفته
     * =====================================================
     */

    val dayOfWeekText =
        when (selectedDate) {

            today.minusDays(2),
            today.minusDays(1),
            today,
            today.plusDays(1),
            today.plusDays(2) ->

                getPersianDayOfWeek(
                    selectedDate
                )

            else ->
                ""
        }


    /*
     * =====================================================
     * تاریخ شمسی
     * =====================================================
     */

    val persianDateText =
        getPersianDateWithPersianDigits(
            selectedDate
        )


    /*
     * =====================================================
     * انیمیشن کشوی روتین
     * =====================================================
     */

    val routineDrawerOffset by animateDpAsState(
        targetValue = if (routineDrawerExpanded) 0.dp else 8.dp,
        animationSpec = tween(
            durationMillis = 300,
            easing = FastOutSlowInEasing
        ),
        label = "routineDrawerOffset"
    )


    /*
     * =====================================================
     * صفحه
     * =====================================================
     */

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.White
            )
    ) {

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(
                    horizontal = 20.dp,
                    vertical = 12.dp
                )
        ) {

            /*
             * =================================================
             * انتخاب روز
             * =================================================
             */

            Card(
                modifier =
                    Modifier.fillMaxWidth(),

                shape =
                    RoundedCornerShape(18.dp),

                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            Color.White
                    ),

                elevation =
                    CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
            ) {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = 10.dp,
                            vertical = 9.dp
                        ),

                    verticalAlignment =
                        Alignment.CenterVertically
                ) {

                    /*
                     * روز قبل
                     */

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .combinedClickable(
                                onClick = {

                                    if (
                                        canGoToPreviousDate
                                    ) {

                                        planningViewModel
                                            .selectPreviousDate()
                                    }
                                },

                                onLongClick = {}
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ChevronLeft,

                            contentDescription =
                                "روز قبل",

                            tint =
                                if (
                                    canGoToPreviousDate
                                ) {
                                    Color(0xFF777777)
                                } else {
                                    Color(0xFFD0D0D0)
                                },

                            modifier =
                                Modifier.size(25.dp)
                        )
                    }


                    /*
                     * آیکون تقویم
                     */

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                color =
                                    Color(0xFFE8F1FF),

                                shape =
                                    RoundedCornerShape(11.dp)
                            )
                            .combinedClickable(
                                onClick =
                                    onDateClick,

                                onLongClick = {}
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.CalendarMonth,

                            contentDescription =
                                "انتخاب روز",

                            tint =
                                Color(0xFF3976D3),

                            modifier =
                                Modifier.size(21.dp)
                        )
                    }


                    Spacer(
                        modifier =
                            Modifier.width(10.dp)
                    )


                    /*
                     * اطلاعات تاریخ
                     */

                    Column(
                        modifier =
                            Modifier.weight(1f),

                        horizontalAlignment =
                            Alignment.CenterHorizontally
                    ) {

                        if (
                            relativeDayText.isNotEmpty()
                        ) {

                            Text(
                                text =
                                    relativeDayText,

                                style =
                                    MaterialTheme
                                        .typography
                                        .labelMedium,

                                color =
                                    Color(0xFF777777),

                                fontWeight =
                                    FontWeight.Medium
                            )
                        }


                        if (
                            dayOfWeekText.isNotEmpty()
                        ) {

                            Spacer(
                                modifier =
                                    Modifier.height(1.dp)
                            )

                            Text(
                                text =
                                    dayOfWeekText,

                                style =
                                    MaterialTheme
                                        .typography
                                        .titleSmall,

                                color =
                                    TextPrimary,

                                fontWeight =
                                    FontWeight.Bold
                            )
                        }


                        Spacer(
                            modifier =
                                Modifier.height(1.dp)
                        )

                        Text(
                            text =
                                persianDateText,

                            style =
                                MaterialTheme
                                    .typography
                                    .bodySmall,

                            color =
                                Color(0xFF666666),

                            fontWeight =
                                FontWeight.Medium
                        )
                    }


                    /*
                     * روز بعد
                     */

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .combinedClickable(
                                onClick = {

                                    planningViewModel
                                        .selectNextDate()
                                },

                                onLongClick = {}
                            ),

                        contentAlignment =
                            Alignment.Center
                    ) {

                        Icon(
                            imageVector =
                                Icons.Default.ChevronRight,

                            contentDescription =
                                "روز بعد",

                            tint =
                                Color(0xFF777777),

                            modifier =
                                Modifier.size(25.dp)
                        )
                    }
                }
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            /*
             * =================================================
             * Header دسته‌ها + سبد
             * =================================================
             */

            Row(
                modifier =
                    Modifier.fillMaxWidth(),

                verticalAlignment =
                    Alignment.CenterVertically
            ) {

                Column(
                    modifier =
                        Modifier.weight(1f)
                ) {

                    Text(
                        text =
                            "دسته‌های فعالیت",

                        style =
                            MaterialTheme
                                .typography
                                .titleMedium,

                        color =
                            TextPrimary,

                        fontWeight =
                            FontWeight.SemiBold
                    )

                    Spacer(
                        modifier =
                            Modifier.height(1.dp)
                    )

                    Text(
                        text =
                            "یک دسته را انتخاب کنید",

                        style =
                            MaterialTheme
                                .typography
                                .bodySmall,

                        color =
                            Color(0xFF858585)
                    )
                }


                /*
                 * سبد
                 */

                PlanningBasket(
                    totalMinutes =
                        totalMinutes,

                    planningViewModel =
                        planningViewModel,

                    selectedDate =
                        selectedDate.toString(),

                    modifier =
                        Modifier.padding(
                            start = 8.dp
                        )
                )
            }


            Spacer(
                modifier =
                    Modifier.height(10.dp)
            )


            /*
             * =================================================
             * Grid
             * =================================================
             */

            LazyVerticalGrid(
                columns =
                    GridCells.Fixed(3),

                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),

                horizontalArrangement =
                    Arrangement.spacedBy(8.dp),

                verticalArrangement =
                    Arrangement.spacedBy(8.dp),

                userScrollEnabled =
                    true
            ) {

                items(
                    items =
                        gridItems,

                    key = { item ->

                        when (item) {

                            is ActivityGridItem.RealCategory ->
                                "real_${item.category.id}"

                            is ActivityGridItem.DefaultCategory ->
                                "default_${item.name}"

                            ActivityGridItem.NewCategory ->
                                "new_category"
                        }
                    }
                ) { item ->

                    when (item) {

                        is ActivityGridItem.RealCategory -> {

                            RealCategoryCard(
                                category =
                                    item.category,

                                onClick = {

                                    onCategoryClick(
                                        item.category
                                    )
                                },

                                onLongClick = {

                                    onCategoryLongClick(
                                        item.category
                                    )
                                }
                            )
                        }


                        is ActivityGridItem.DefaultCategory -> {

                            DefaultCategoryCard(
                                title =
                                    item.name,

                                onClick = {

                                    onAddCategory(
                                        item.name,
                                        ""
                                    )
                                }
                            )
                        }


                        ActivityGridItem.NewCategory -> {

                            NewCategoryAction(
                                onClick =
                                    onOtherCategoryClick
                            )
                        }
                    }
                }
            }
        }


        /*
         * =====================================================
         * لایه‌ی کلیک برای بستن کشو
         *
         * وقتی کشو باز است، هر جای خالی صفحه
         * که خود کشو نباشد، باعث بسته شدن آن می‌شود.
         * =====================================================
         */

        if (routineDrawerExpanded) {

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(Unit) {
                        detectTapGestures {
                            routineDrawerExpanded = false
                        }
                    }
            )
        }


        /*
         * =====================================================
         * کشوی روتین
         * =====================================================
         */

        RoutineDrawer(
            expanded =
                routineDrawerExpanded,

            offsetX =
                routineDrawerOffset,

            onClick = {

                if (routineDrawerExpanded) {

                    onRoutinePlanningClick()

                } else {

                    routineDrawerExpanded =
                        true
                }
            }
        )
    }
}


/*
 * =====================================================
 * کشوی روتین
 * =====================================================
 */

@Composable
private fun RoutineDrawer(
    expanded: Boolean,
    offsetX: androidx.compose.ui.unit.Dp,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 12.dp)
            .offset(x = offsetX),
        contentAlignment = Alignment.TopEnd
    ) {
        Box(
            modifier = Modifier
                .width(if (expanded) 118.dp else 42.dp)
                .height(if (expanded) 76.dp else 76.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF008F3D),
                            Color(0xFF00752F),
                            Color(0xFF006126)
                        )
                    ),
                    shape = RoundedCornerShape(
                        topStart = 18.dp,
                        bottomStart = 18.dp,
                        topEnd = 0.dp,
                        bottomEnd = 0.dp
                    )
                )
                .combinedClickable(
                    onClick = onClick,
                    onLongClick = {}
                ),
            contentAlignment = Alignment.Center
        ) {
            if (expanded) {
                Text(
                    text = "روتین‌ها",
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 14.sp
                )
            } else {
                Text(
                    text = "روتین‌ها",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black,
                    fontWeight = FontWeight.Medium,
                    fontSize = 11.sp,
                    modifier = Modifier.rotate(-90f)
                )
            }
        }
    }
}


/*
 * =====================================================
 * تبدیل نام روز هفته به فارسی
 * =====================================================
 */

private fun getPersianDayOfWeek(
    date: LocalDate
): String {

    return when (date.dayOfWeek) {

        DayOfWeek.SATURDAY ->
            "شنبه"

        DayOfWeek.SUNDAY ->
            "یکشنبه"

        DayOfWeek.MONDAY ->
            "دوشنبه"

        DayOfWeek.TUESDAY ->
            "سه‌شنبه"

        DayOfWeek.WEDNESDAY ->
            "چهارشنبه"

        DayOfWeek.THURSDAY ->
            "پنجشنبه"

        DayOfWeek.FRIDAY ->
            "جمعه"
    }
}


/*
 * =====================================================
 * تبدیل اعداد انگلیسی به فارسی
 * =====================================================
 */

private fun toPersianDigits(
    value: String
): String {

    return value
        .replace("0", "۰")
        .replace("1", "۱")
        .replace("2", "۲")
        .replace("3", "۳")
        .replace("4", "۴")
        .replace("5", "۵")
        .replace("6", "۶")
        .replace("7", "۷")
        .replace("8", "۸")
        .replace("9", "۹")
}


/*
 * =====================================================
 * تاریخ شمسی با اعداد فارسی
 * =====================================================
 */

private fun getPersianDateWithPersianDigits(
    date: LocalDate
): String {

    return toPersianDigits(
        getPersianDate(date)
    )
}


/*
 * =====================================================
 * Grid Item
 * =====================================================
 */

private sealed class ActivityGridItem {

    data class RealCategory(
        val category: Category
    ) : ActivityGridItem()


    data class DefaultCategory(
        val name: String
    ) : ActivityGridItem()


    data object NewCategory :
        ActivityGridItem()
}


/*
 * =====================================================
 * Real Category
 * =====================================================
 */

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RealCategoryCard(
    category: Category,
    onClick: () -> Unit,
    onLongClick: () -> Unit
) {

    CategoryVisualCard(
        title =
            category.name,

        onClick =
            onClick,

        onLongClick =
            onLongClick,

        modifier =
            Modifier.fillMaxWidth()
    )
}


/*
 * =====================================================
 * Default Category
 * =====================================================
 */

@Composable
private fun DefaultCategoryCard(
    title: String,
    onClick: () -> Unit
) {

    CategoryVisualCard(
        title =
            title,

        onClick =
            onClick,

        modifier =
            Modifier.fillMaxWidth()
    )
}


/*
 * =====================================================
 * New Category
 * =====================================================
 */

@Composable
private fun NewCategoryAction(
    onClick: () -> Unit
) {

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(118.dp)
            .combinedClickable(
                onClick =
                    onClick,

                onLongClick = {}
            ),

        contentAlignment =
            Alignment.Center
    ) {

        Box(
            modifier = Modifier
                .size(62.dp)
                .background(
                    color =
                        Color(0xFFE8EBEF),

                    shape =
                        RoundedCornerShape(18.dp)
                ),

            contentAlignment =
                Alignment.Center
        ) {

            Icon(
                imageVector =
                    Icons.Default.Add,

                contentDescription =
                    "ایجاد دسته جدید",

                tint =
                    Color(0xFF59636E),

                modifier =
                    Modifier.size(34.dp)
            )
        }
    }
}