package com.example.yekdarsad.ui.overview

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.yekdarsad.ui.theme.CardBackground
import com.example.yekdarsad.ui.theme.PrimaryGreen
import com.example.yekdarsad.ui.theme.PrimaryGreenLight
import com.example.yekdarsad.ui.theme.TextSecondary
import kotlinx.coroutines.launch

@Composable
fun OverviewNoteButton(
    modifier: Modifier = Modifier,
    hasNote: Boolean,
    onClick: () -> Unit
) {

    Box(
        modifier = modifier
            .clip(
                RoundedCornerShape(50.dp)
            )
            .background(
                if (hasNote) {
                    PrimaryGreenLight.copy(
                        alpha = 0.65f
                    )
                } else {
                    CardBackground.copy(
                        alpha = 0.85f
                    )
                }
            )
            .border(
                width = 0.7.dp,
                color = PrimaryGreen.copy(
                    alpha = if (hasNote) {
                        0.35f
                    } else {
                        0.18f
                    }
                ),
                shape = RoundedCornerShape(50.dp)
            )
            .clickable {
                onClick()
            }
            .padding(
                horizontal = 11.dp,
                vertical = 5.dp
            )
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {

            Icon(
                imageVector = Icons.Default.EditNote,
                contentDescription = "یادداشت",
                modifier = Modifier.size(15.dp),
                tint = PrimaryGreen
            )

            Text(
                text = if (hasNote) {
                    "یادداشت"
                } else {
                    "افزودن یادداشت"
                },
                fontSize = 8.sp,
                fontWeight = FontWeight.Medium,
                color = PrimaryGreen
            )
        }
    }
}

@Composable
fun OverviewNoteOverlay(
    title: String,
    initialText: String,
    onDismiss: () -> Unit,
    onSave: suspend (String) -> Unit,
    onDelete: suspend () -> Unit
) {

    var text by remember {
        mutableStateOf(initialText)
    }

    var isSaving by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    val keyboardController =
        LocalSoftwareKeyboardController.current

    LaunchedEffect(initialText) {
        text = initialText
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Color.Black.copy(
                    alpha = 0.10f
                )
            )
            .clickable(
                indication = null,
                interactionSource = remember {
                    MutableInteractionSource()
                }
            ) {

                keyboardController?.hide()

                onDismiss()
            }
    ) {

        Box(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(0.88f)
                .clip(
                    RoundedCornerShape(22.dp)
                )
                .background(
                    CardBackground.copy(
                        alpha = 0.98f
                    )
                )
                .border(
                    width = 0.8.dp,
                    color = PrimaryGreen.copy(
                        alpha = 0.20f
                    ),
                    shape = RoundedCornerShape(22.dp)
                )
                .clickable(
                    indication = null,
                    interactionSource = remember {
                        MutableInteractionSource()
                    }
                ) {
                    // جلوگیری از بسته شدن Overlay
                }
                .padding(16.dp)
        ) {

            Column {

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null,
                        modifier = Modifier.size(22.dp),
                        tint = PrimaryGreen
                    )

                    Spacer(
                        modifier = Modifier.size(7.dp)
                    )

                    Text(
                        text = title,
                        modifier = Modifier.weight(1f),
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )

                    IconButton(
                        onClick = {

                            keyboardController?.hide()

                            onDismiss()
                        },
                        modifier = Modifier.size(32.dp)
                    ) {

                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "بستن",
                            modifier = Modifier.size(19.dp),
                            tint = TextSecondary
                        )
                    }
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(
                            RoundedCornerShape(15.dp)
                        )
                        .background(
                            Color.White.copy(
                                alpha = 0.85f
                            )
                        )
                        .border(
                            width = 0.7.dp,
                            color = PrimaryGreen.copy(
                                alpha = 0.13f
                            ),
                            shape = RoundedCornerShape(15.dp)
                        )
                        .padding(12.dp)
                ) {

                    BasicTextField(
                        value = text,

                        onValueChange = {
                            text = it
                        },

                        modifier = Modifier.fillMaxSize(),

                        textStyle = TextStyle(
                            fontSize = 12.sp,
                            color = Color.Black,
                            lineHeight = 20.sp
                        ),

                        cursorBrush = SolidColor(
                            PrimaryGreen
                        ),

                        decorationBox = { innerTextField ->

                            if (text.isEmpty()) {

                                Text(
                                    text =
                                        "اینجا چیزی برای این بازه بنویس...\n\n" +
                                                "مثلاً:\n" +
                                                "این ماه عملکرد خوبی داشتم، ولی باید روی مطالعه بیشتر تمرکز کنم.",

                                    fontSize = 11.sp,

                                    lineHeight = 19.sp,

                                    color =
                                        TextSecondary.copy(
                                            alpha = 0.75f
                                        )
                                )
                            }

                            innerTextField()
                        }
                    )
                }

                Spacer(
                    modifier = Modifier.height(12.dp)
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    if (initialText.isNotBlank()) {

                        Box(
                            modifier = Modifier
                                .clip(
                                    RoundedCornerShape(12.dp)
                                )
                                .background(
                                    Color.Red.copy(
                                        alpha = 0.07f
                                    )
                                )
                                .clickable(
                                    enabled = !isSaving
                                ) {

                                    isSaving = true

                                    keyboardController?.hide()

                                    scope.launch {

                                        try {

                                            onDelete()

                                            onDismiss()

                                        } finally {

                                            isSaving = false
                                        }
                                    }
                                }
                                .padding(
                                    horizontal = 12.dp,
                                    vertical = 9.dp
                                )
                        ) {

                            Row(
                                verticalAlignment =
                                    Alignment.CenterVertically,

                                horizontalArrangement =
                                    Arrangement.spacedBy(4.dp)
                            ) {

                                Icon(
                                    imageVector =
                                        Icons.Default.DeleteOutline,

                                    contentDescription =
                                        "حذف",

                                    modifier =
                                        Modifier.size(16.dp),

                                    tint =
                                        Color.Red.copy(
                                            alpha = 0.75f
                                        )
                                )

                                Text(
                                    text = "حذف",

                                    fontSize = 9.sp,

                                    color =
                                        Color.Red.copy(
                                            alpha = 0.75f
                                        )
                                )
                            }
                        }
                    }

                    Spacer(
                        modifier = Modifier.weight(1f)
                    )

                    Box(
                        modifier = Modifier
                            .clip(
                                RoundedCornerShape(12.dp)
                            )
                            .background(
                                PrimaryGreen
                            )
                            .clickable(
                                enabled = !isSaving
                            ) {

                                isSaving = true

                                keyboardController?.hide()

                                scope.launch {

                                    try {

                                        onSave(
                                            text.trim()
                                        )

                                        onDismiss()

                                    } finally {

                                        isSaving = false
                                    }
                                }
                            }
                            .padding(
                                horizontal = 15.dp,
                                vertical = 9.dp
                            )
                    ) {

                        Row(
                            verticalAlignment =
                                Alignment.CenterVertically,

                            horizontalArrangement =
                                Arrangement.spacedBy(5.dp)
                        ) {

                            Icon(
                                imageVector =
                                    Icons.Default.Save,

                                contentDescription =
                                    "ذخیره",

                                modifier =
                                    Modifier.size(16.dp),

                                tint =
                                    Color.White
                            )

                            Text(
                                text =
                                    if (isSaving) {
                                        "در حال ذخیره..."
                                    } else {
                                        "ذخیره"
                                    },

                                fontSize = 9.sp,

                                fontWeight =
                                    FontWeight.Bold,

                                color =
                                    Color.White
                            )
                        }
                    }
                }
            }
        }
    }
}