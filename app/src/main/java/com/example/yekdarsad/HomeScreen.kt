package com.example.yekdarsad

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.verticalScroll
import androidx.compose.animation.togetherWith
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AutoAwesome
import androidx.compose.material.icons.outlined.Book
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.DirectionsRun
import androidx.compose.material.icons.outlined.EmojiEvents
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.FitnessCenter
import androidx.compose.material.icons.outlined.Lightbulb
import androidx.compose.material.icons.outlined.MenuBook
import androidx.compose.material.icons.outlined.MilitaryTech
import androidx.compose.material.icons.outlined.Nightlight
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material.icons.outlined.Spa
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.outlined.TrendingUp
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.material.icons.outlined.WbSunny
import androidx.compose.material.icons.outlined.WorkspacePremium
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

private val HomeGreen = Color(0xFF496A42)
private val HomeGreenLight = Color(0xFFEAF0E6)
private val HomeText = Color(0xFF252525)
private val HomeSecondaryText = Color(0xFF777777)
private val HomeBorder = Color(0xFFEEEEEE)

/* ========================================================================== */
/* HOME SCREEN                                                                */
/* ========================================================================== */

@Composable
fun HomeScreen(
    onTodayClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onOverviewClick: () -> Unit,
    onActivitiesClick: () -> Unit
) {
    var currentInsightIndex by remember {
        mutableIntStateOf(0)
    }

    var selectedChallenge by remember {
        mutableStateOf<HomeChallenge?>(null)
    }

    val insights = remember {
        homeInsights()
    }

    val challenges = remember {
        homeChallenges()
    }

    val currentInsight = insights[currentInsightIndex]

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            HomeTopHeader()

            HomeStories(
                onTodayClick = onTodayClick,
                onStatisticsClick = onStatisticsClick,
                onOverviewClick = onOverviewClick,
                onActivitiesClick = onActivitiesClick
            )

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
            ) {
                Spacer(modifier = Modifier.height(22.dp))

                InsightCard(
                    insight = currentInsight,
                    onNext = {
                        currentInsightIndex =
                            (currentInsightIndex + 1) % insights.size
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                HomeQuickActions()

                Spacer(modifier = Modifier.height(28.dp))

                ChallengeSection(
                    challenges = challenges,
                    onChallengeClick = {
                        selectedChallenge = it
                    }
                )

                Spacer(modifier = Modifier.height(28.dp))

                DailyMissionCard()

                Spacer(modifier = Modifier.height(36.dp))
            }
        }

        selectedChallenge?.let { challenge ->
            ChallengeDetailsOverlay(
                challenge = challenge,
                onDismiss = {
                    selectedChallenge = null
                }
            )
        }
    }
}

/* ========================================================================== */
/* HEADER                                                                     */
/* ========================================================================== */

@Composable
private fun HomeTopHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.White)
            .padding(
                start = 20.dp,
                end = 20.dp,
                top = 22.dp,
                bottom = 14.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "خانه",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = HomeText
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = "امروزت رو بهتر از دیروز بساز",
                style = MaterialTheme.typography.bodyMedium,
                color = HomeSecondaryText
            )
        }

        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(HomeGreenLight),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.AutoAwesome,
                contentDescription = null,
                tint = HomeGreen,
                modifier = Modifier.size(23.dp)
            )
        }
    }
}

/* ========================================================================== */
/* STORIES                                                                    */
/* ========================================================================== */

private data class HomeStory(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit
)

@Composable
private fun HomeStories(
    onTodayClick: () -> Unit,
    onStatisticsClick: () -> Unit,
    onOverviewClick: () -> Unit,
    onActivitiesClick: () -> Unit
) {
    val stories = listOf(
        HomeStory(
            title = "امروز",
            icon = Icons.Outlined.CheckCircle,
            onClick = onTodayClick
        ),
        HomeStory(
            title = "آمار",
            icon = Icons.Outlined.TrendingUp,
            onClick = onStatisticsClick
        ),
        HomeStory(
            title = "در یک نگاه",
            icon = Icons.Outlined.AutoAwesome,
            onClick = onOverviewClick
        ),
        HomeStory(
            title = "برنامه‌ریزی",
            icon = Icons.Outlined.MenuBook,
            onClick = onActivitiesClick
        )
    )

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp, bottom = 4.dp),
        contentPadding = androidx.compose.foundation.layout.PaddingValues(
            horizontal = 20.dp
        ),
        horizontalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        items(
            items = stories,
            key = { it.title }
        ) { story ->
            Column(
                modifier = Modifier
                    .width(66.dp)
                    .clickable(onClick = story.onClick),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(HomeGreenLight)
                        .padding(2.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(3.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .clip(CircleShape)
                                .background(HomeGreenLight),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = story.icon,
                                contentDescription = story.title,
                                tint = HomeGreen,
                                modifier = Modifier.size(25.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(7.dp))

                Text(
                    text = story.title,
                    style = MaterialTheme.typography.labelSmall,
                    color = HomeText,
                    maxLines = 1
                )
            }
        }
    }
}

/* ========================================================================== */
/* INSIGHT CARD                                                               */
/* ========================================================================== */

@Composable
private fun InsightCard(
    insight: HomeInsight,
    onNext: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            HomeBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                InsightIcon(type = insight.type)

                Spacer(modifier = Modifier.weight(1f))

                Text(
                    text = "نکته روز",
                    style = MaterialTheme.typography.labelMedium,
                    color = HomeSecondaryText
                )

                IconButton(onClick = onNext) {
                    Icon(
                        imageVector = Icons.Outlined.AutoAwesome,
                        contentDescription = "نکته بعدی",
                        tint = HomeGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            AnimatedContent(
                targetState = insight,
                transitionSpec = {
                    (
                            fadeIn(animationSpec = tween(250)) +
                                    scaleIn(
                                        initialScale = 0.98f,
                                        animationSpec = tween(250)
                                    )
                            ) togetherWith fadeOut(
                        animationSpec = tween(180)
                    )
                },
                label = "insight_animation"
            ) { item ->
                Column {
                    Text(
                        text = item.title,
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = HomeText
                    )

                    Spacer(modifier = Modifier.height(9.dp))

                    Text(
                        text = item.message,
                        style = MaterialTheme.typography.bodyMedium,
                        color = HomeSecondaryText
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            InsightDots(
                selected = insight.id % 3
            )
        }
    }
}

/* ========================================================================== */
/* INSIGHT ICON                                                               */
/* ========================================================================== */

@Composable
private fun InsightIcon(
    type: HomeInsightType
) {
    val icon = when (type) {
        HomeInsightType.INFO -> Icons.Outlined.Lightbulb
        HomeInsightType.TREND -> Icons.Outlined.TrendingUp
        HomeInsightType.MOTIVATION -> Icons.Outlined.Favorite
        HomeInsightType.CHALLENGE -> Icons.Outlined.EmojiEvents
        HomeInsightType.WARNING -> Icons.Outlined.AutoAwesome
        HomeInsightType.ACHIEVEMENT -> Icons.Outlined.WorkspacePremium
        HomeInsightType.FUNNY -> Icons.Outlined.AutoAwesome
    }

    Box(
        modifier = Modifier
            .size(48.dp)
            .clip(RoundedCornerShape(15.dp))
            .background(HomeGreenLight),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = HomeGreen,
            modifier = Modifier.size(24.dp)
        )
    }
}

/* ========================================================================== */
/* INSIGHT DOTS                                                               */
/* ========================================================================== */

@Composable
private fun InsightDots(
    selected: Int
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(3) { index ->
            Box(
                modifier = Modifier
                    .height(4.dp)
                    .width(
                        if (index == selected) 26.dp else 7.dp
                    )
                    .clip(RoundedCornerShape(50))
                    .background(
                        if (index == selected) HomeGreen
                        else HomeBorder
                    )
            )

            if (index != 2) {
                Spacer(modifier = Modifier.width(6.dp))
            }
        }
    }
}

/* ========================================================================== */
/* QUICK ACTIONS                                                              */
/* ========================================================================== */

@Composable
private fun HomeQuickActions() {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "یه انتخاب برای امروز",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold
            ),
            color = HomeText
        )

        Spacer(modifier = Modifier.height(12.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            QuickActionCard(
                icon = Icons.Outlined.MenuBook,
                title = "۲۰ دقیقه مطالعه",
                subtitle = "یه شروع کوچیک"
            )

            QuickActionCard(
                icon = Icons.Outlined.DirectionsRun,
                title = "کمی تحرک",
                subtitle = "بدنت رو بیدار کن"
            )

            QuickActionCard(
                icon = Icons.Outlined.DarkMode,
                title = "آماده خواب",
                subtitle = "امشب بهتر بخواب"
            )
        }
    }
}

/* ========================================================================== */
/* QUICK ACTION CARD                                                          */
/* ========================================================================== */

@Composable
private fun QuickActionCard(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Card(
        modifier = Modifier
            .width(190.dp)
            .height(108.dp)
            .clickable {
                // فعلاً نمایشی است؛ عملکرد واقعی بعداً متصل می‌شود.
            },
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            HomeBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(13.dp))
                    .background(HomeGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HomeGreen,
                    modifier = Modifier.size(22.dp)
                )
            }

            Spacer(modifier = Modifier.width(10.dp))

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = HomeText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = HomeSecondaryText
                )
            }
        }
    }
}

/* ========================================================================== */
/* CHALLENGES                                                                 */
/* ========================================================================== */

@Composable
private fun ChallengeSection(
    challenges: List<HomeChallenge>,
    onChallengeClick: (HomeChallenge) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = "چالش‌ها",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = HomeText
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "یه چالش انتخاب کن و خودت رو امتحان کن",
                    style = MaterialTheme.typography.bodySmall,
                    color = HomeSecondaryText
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(HomeGreenLight),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.EmojiEvents,
                    contentDescription = null,
                    tint = HomeGreen
                )
            }
        }

        Spacer(modifier = Modifier.height(15.dp))

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(
                items = challenges,
                key = { it.id }
            ) { challenge ->
                ChallengeCard(
                    challenge = challenge,
                    onClick = {
                        onChallengeClick(challenge)
                    }
                )
            }
        }
    }
}

/* ========================================================================== */
/* CHALLENGE CARD                                                             */
/* ========================================================================== */

@Composable
private fun ChallengeCard(
    challenge: HomeChallenge,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .width(250.dp)
            .height(175.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            HomeBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(17.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(HomeGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = challenge.icon,
                        contentDescription = null,
                        tint = HomeGreen,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(modifier = Modifier.weight(1f))

                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFF5F6F4)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PlayArrow,
                        contentDescription = "جزئیات چالش",
                        tint = HomeGreen,
                        modifier = Modifier.size(19.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(13.dp))

            Text(
                text = challenge.title,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = HomeText
            )

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                text = challenge.description,
                style = MaterialTheme.typography.bodySmall,
                color = HomeSecondaryText
            )

            Spacer(modifier = Modifier.weight(1f))

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${challenge.days} روز",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = HomeGreen
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = "•",
                    color = Color.LightGray
                )

                Spacer(modifier = Modifier.width(8.dp))

                Text(
                    text = challenge.category,
                    style = MaterialTheme.typography.labelSmall,
                    color = HomeSecondaryText
                )
            }
        }
    }
}

/* ========================================================================== */
/* DAILY MISSION                                                              */
/* ========================================================================== */

@Composable
private fun DailyMissionCard() {
    var completed by remember {
        mutableStateOf(false)
    }

    val background = if (completed) {
        HomeGreenLight
    } else {
        Color.White
    }

    val icon = if (completed) {
        Icons.Outlined.CheckCircle
    } else {
        Icons.Outlined.Star
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .clickable {
                completed = !completed
            },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = background
        ),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (completed) HomeGreenLight else HomeBorder
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 0.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(15.dp))
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = HomeGreen,
                    modifier = Modifier.size(25.dp)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = if (completed) {
                        "انجامش دادی!"
                    } else {
                        "مأموریت کوچیک امروز"
                    },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = HomeText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = if (completed) {
                        "همین قدم‌های کوچیک مسیرت رو می‌سازن."
                    } else {
                        "امروز فقط ۲۰ دقیقه روی مهم‌ترین کارت تمرکز کن."
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = HomeSecondaryText
                )
            }
        }
    }
}

/* ========================================================================== */
/* CHALLENGE OVERLAY                                                          */
/* ========================================================================== */

@Composable
private fun ChallengeDetailsOverlay(
    challenge: HomeChallenge,
    onDismiss: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.35f))
            .clickable {
                onDismiss()
            },
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.62f)
                .clickable {
                    // لمس داخل کارت، آن را نمی‌بندد.
                },
            shape = RoundedCornerShape(
                topStart = 28.dp,
                topEnd = 28.dp
            ),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = 0.dp
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp)
            ) {
                Box(
                    modifier = Modifier
                        .width(42.dp)
                        .height(5.dp)
                        .clip(CircleShape)
                        .background(HomeBorder)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(25.dp))

                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(HomeGreenLight),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = challenge.icon,
                        contentDescription = null,
                        tint = HomeGreen,
                        modifier = Modifier.size(30.dp)
                    )
                }

                Spacer(modifier = Modifier.height(18.dp))

                Text(
                    text = challenge.title,
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = HomeText
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = challenge.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = HomeSecondaryText
                )

                Spacer(modifier = Modifier.height(22.dp))

                Text(
                    text = "مدت چالش",
                    style = MaterialTheme.typography.labelLarge,
                    color = HomeSecondaryText
                )

                Spacer(modifier = Modifier.height(5.dp))

                Text(
                    text = "${challenge.days} روز",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold
                    ),
                    color = HomeGreen
                )

                Spacer(modifier = Modifier.weight(1f))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = HomeGreen
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                // شروع واقعی چالش هنوز پیاده‌سازی نشده است.
                            }
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "شروع چالش",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold
                            ),
                            color = Color.White
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "فعلاً نمایشی است؛ اتصال به سیستم چالش‌ها در مرحله بعد انجام می‌شود.",
                    modifier = Modifier.fillMaxWidth(),
                    style = MaterialTheme.typography.bodySmall,
                    color = HomeSecondaryText
                )
            }
        }
    }
}

/* ========================================================================== */
/* INSIGHT DATA                                                               */
/* ========================================================================== */

private fun homeInsights(): List<HomeInsight> {
    return listOf(
        HomeInsight(0, HomeInsightType.INFO, "یه نکته برای تو",
            "هر روز فقط یک درصد بهتر شدن، در طول زمان تفاوت بزرگی ایجاد می‌کنه."),
        HomeInsight(1, HomeInsightType.MOTIVATION, "قرار نیست کامل باشی",
            "قرار نیست هر روز فوق‌العاده باشی. فقط نذار یک روز بد تبدیل به چند روز بد بشه."),
        HomeInsight(2, HomeInsightType.TREND, "به روندت نگاه کن",
            "یک روز خوب یا بد چیزی رو تعیین نمی‌کنه؛ چیزی که مهمه مسیریه که در طول زمان می‌سازی."),
        HomeInsight(3, HomeInsightType.CHALLENGE, "یه چالش کوچیک",
            "امروز فقط یکی از کارهایی که مدت‌ها عقب انداختی رو انجام بده."),
        HomeInsight(4, HomeInsightType.MOTIVATION, "شروع کن",
            "لازم نیست انگیزه داشته باشی تا شروع کنی. خیلی وقت‌ها انگیزه بعد از شروع میاد."),
        HomeInsight(5, HomeInsightType.FUNNY, "یه لحظه 😅",
            "اون کاری که گفتی «بعداً انجام میدم» هنوز هم منتظرته."),
        HomeInsight(6, HomeInsightType.INFO, "قانون ساده",
            "اگر کاری کمتر از چند دقیقه زمان می‌بره، بذار انجام بشه و از ذهنت خارجش کن."),
        HomeInsight(7, HomeInsightType.MOTIVATION, "فقط امروز",
            "به جای اینکه به سی روز آینده فکر کنی، فقط امروز رو درست انجام بده."),
        HomeInsight(8, HomeInsightType.CHALLENGE, "۲۰ دقیقه تمرکز",
            "گوشی رو کنار بذار و فقط بیست دقیقه روی یک کار تمرکز کن."),
        HomeInsight(9, HomeInsightType.INFO, "کوچیک شروع کن",
            "کار بزرگ رو به کوچک‌ترین قدم ممکن تبدیل کن. بعد فقط همون قدم اول رو بردار."),
        HomeInsight(10, HomeInsightType.MOTIVATION, "برگرد",
            "اگر چند روز از مسیر خارج شدی، لازم نیست از اول شروع کنی؛ فقط دوباره ادامه بده."),
        HomeInsight(11, HomeInsightType.ACHIEVEMENT, "هر قدم حساب میشه",
            "حتی یک کار کوچک که انجامش دادی، بهتر از کاریه که فقط درباره‌ش فکر کردی."),
        HomeInsight(12, HomeInsightType.FUNNY, "گوشی یه طرف 😏",
            "فقط پنج دقیقه اومدی گوشی رو چک کنی؟ آره... خودمون می‌دونیم."),
        HomeInsight(13, HomeInsightType.MOTIVATION, "خودت رو مقایسه نکن",
            "رقیب اصلیت کسی نیست که از تو جلوتره؛ نسخه دیروز خودته."),
        HomeInsight(14, HomeInsightType.INFO, "ثبات از هیجان مهم‌تره",
            "کاری که هر روز کمی انجام میدی، از کاری که گاهی با شدت زیاد انجام میدی ماندگارتره."),
        HomeInsight(15, HomeInsightType.CHALLENGE, "امروز بدون بهانه",
            "یک کاری که معمولاً برای انجام ندادنش بهانه میاری رو همین امروز انجام بده."),
        HomeInsight(16, HomeInsightType.MOTIVATION, "یک درصد",
            "قرار نیست یک شبه تغییر کنی. فقط هر روز کمی بهتر."),
        HomeInsight(17, HomeInsightType.INFO, "محیطت مهمه",
            "اگر انجام دادن یک کار سخته، محیطت رو طوری بچین که انجام دادنش راحت‌تر بشه."),
        HomeInsight(18, HomeInsightType.CHALLENGE, "چالش بدون حواس‌پرتی",
            "یک بازه کوتاه انتخاب کن و در اون هیچ شبکه اجتماعی‌ای رو باز نکن."),
        HomeInsight(19, HomeInsightType.MOTIVATION, "تو هنوز تو بازی‌ای",
            "تا وقتی ادامه میدی، بازی تموم نشده."),
        HomeInsight(20, HomeInsightType.INFO, "یک تصمیم کوچک",
            "امروز فقط یک تصمیم بگیر که فردای تو رو کمی راحت‌تر کنه."),
        HomeInsight(21, HomeInsightType.FUNNY, "من دیدم 👀",
            "هیچ‌کس نمی‌دونه اون پنج دقیقه تبدیل به یک ساعت شد یا نه... ولی خودت می‌دونی."),
        HomeInsight(22, HomeInsightType.MOTIVATION, "آروم ولی پیوسته",
            "سرعت مهم نیست؛ مهم اینه که متوقف نشی."),
        HomeInsight(23, HomeInsightType.CHALLENGE, "امروز یک نه بگو",
            "به چیزی که وقتت رو بی‌دلیل می‌گیره، امروز یک بار «نه» بگو."),
        HomeInsight(24, HomeInsightType.ACHIEVEMENT, "به خودت اعتبار بده",
            "گاهی آنقدر روی کارهایی که انجام ندادی تمرکز می‌کنی که کارهایی که انجام دادی رو نمی‌بینی.")
    )
}

/* ========================================================================== */
/* CHALLENGE DATA                                                             */
/* ========================================================================== */

private fun homeChallenges(): List<HomeChallenge> {
    return listOf(
        HomeChallenge(1, "۷ روز زیارت عاشورا", "هفت روز بدون قطع زنجیره",
            7, "معنوی", Icons.Outlined.SelfImprovement, Color(0xFF43A047)),
        HomeChallenge(2, "۷ روز مطالعه", "هر روز حداقل ۲۰ دقیقه",
            7, "یادگیری", Icons.Outlined.MenuBook, Color(0xFFFF9800)),
        HomeChallenge(3, "۱۴ روز تمرکز", "هر روز یک جلسه بدون حواس‌پرتی",
            14, "تمرکز", Icons.Outlined.Lightbulb, Color(0xFF7E57C2)),
        HomeChallenge(4, "۷ روز پیاده‌روی", "هر روز کمی تحرک بیشتر",
            7, "ورزش", Icons.Outlined.DirectionsRun, Color(0xFF00ACC1)),
        HomeChallenge(5, "۳۰ روز عادت جدید", "یک عادت کوچک بساز",
            30, "عادت‌سازی", Icons.Outlined.FitnessCenter, Color(0xFFE53935)),
        HomeChallenge(6, "۷ روز آب بیشتر", "آب خوردن رو جدی‌تر بگیر",
            7, "سلامت", Icons.Outlined.WaterDrop, Color(0xFF1E88E5)),
        HomeChallenge(7, "۷ روز خواب منظم", "یک ساعت خواب ثابت",
            7, "خواب", Icons.Outlined.Nightlight, Color(0xFF3949AB)),
        HomeChallenge(8, "۷ روز بدون خرید اضافه", "فقط خریدهای ضروری",
            7, "مالی", Icons.Outlined.Star, Color(0xFF00897B)),
        HomeChallenge(9, "۷ روز زبان انگلیسی", "هر روز یک قدم",
            7, "زبان", Icons.Outlined.Book, Color(0xFFEF5350)),
        HomeChallenge(10, "۷ روز صبح قوی", "روزت رو بدون عجله شروع کن",
            7, "روتین", Icons.Outlined.WbSunny, Color(0xFFFFB300)),
        HomeChallenge(11, "۱۴ روز بدون تنبلی", "هر روز یک کار عقب‌افتاده",
            14, "انضباط", Icons.Outlined.MilitaryTech, Color(0xFF546E7A)),
        HomeChallenge(12, "۷ روز مراقبت از خود", "هر روز یک کار خوب برای خودت",
            7, "خودمراقبتی", Icons.Outlined.Spa, Color(0xFFEC407A))
    )
}

/* ========================================================================== */
/* MODELS                                                                     */
/* ========================================================================== */

private data class HomeInsight(
    val id: Int,
    val type: HomeInsightType,
    val title: String,
    val message: String
)

private enum class HomeInsightType {
    INFO,
    TREND,
    MOTIVATION,
    CHALLENGE,
    WARNING,
    ACHIEVEMENT,
    FUNNY
}

private data class HomeChallenge(
    val id: Int,
    val title: String,
    val description: String,
    val days: Int,
    val category: String,
    val icon: ImageVector,
    val color: Color
)