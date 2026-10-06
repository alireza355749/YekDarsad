package com.example.yekdarsad.ui.components

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDirection
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.data.DailyNote
import com.example.yekdarsad.data.DailyNoteDao
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DailyNoteButton(
    date: String,
    dao: DailyNoteDao
) {

    var open by remember {
        mutableStateOf(false)
    }

    val scope = rememberCoroutineScope()

    var saveJob by remember {
        mutableStateOf<Job?>(null)
    }

    val note by dao
        .getNote(date)
        .collectAsState(initial = null)

    var noteText by remember {
        mutableStateOf("")
    }

    var moshareteText by remember {
        mutableStateOf("")
    }

    var mohasebeText by remember {
        mutableStateOf("")
    }

    LaunchedEffect(note) {

        noteText = note?.note ?: ""

        moshareteText = note?.mosharete ?: ""

        mohasebeText = note?.mohasebe ?: ""
    }

    fun saveNote() {
        saveJob?.cancel()

        saveJob = scope.launch {
            delay(500)

            dao.saveNote(
                DailyNote(
                    date = date,
                    note = noteText,
                    mosharete = moshareteText,
                    mohasebe = mohasebeText,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {

        AnimatedVisibility(
            visible = open,

            enter = scaleIn(
                animationSpec = tween(250),
                initialScale = 0.85f
            ) + fadeIn(),

            exit = scaleOut(
                animationSpec = tween(200),
                targetScale = 0.85f
            ) + fadeOut(),

            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 70.dp,
                    end = 8.dp
                )
        ) {

            Surface(
                modifier = Modifier
                    .width(300.dp)
                    .height(430.dp),

                shape = MaterialTheme.shapes.extraLarge,

                tonalElevation = 8.dp

            ) {

                Column(
                    modifier = Modifier
                        .padding(12.dp),

                    verticalArrangement =
                        Arrangement.spacedBy(8.dp)
                ) {

                    Text(
                        text = "دفتر روزانه",
                        style = MaterialTheme.typography.titleMedium
                    )

                    NoteSection(
                        title = "📖 یادداشت‌ها",
                        value = noteText,
                        onChange = {
                            noteText = it
                            saveNote()
                        }
                    )

                    NoteSection(
                        title = "🎯 مشارطه",
                        value = moshareteText,
                        onChange = {
                            moshareteText = it
                            saveNote()
                        }
                    )

                    NoteSection(
                        title = "✅ محاسبه",
                        value = mohasebeText,
                        onChange = {
                            mohasebeText = it
                            saveNote()
                        }
                    )
                }
            }
        }

        IconButton(
            onClick = {
                open = !open
            },

            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(
                    top = 4.dp,
                    end = 2.dp
                )
                .size(44.dp)
        ) {

            Icon(
                imageVector = Icons.Default.EditNote,

                contentDescription = "Daily Note",

                // آبی واقعی
                tint = Color(0xFF2196F3),

                modifier = Modifier
                    .size(27.dp)
                    .rotate(
                        if (open) 180f else 0f
                    )
            )
        }
    }
}

@Composable
private fun NoteSection(
    title: String,
    value: String,
    onChange: (String) -> Unit
) {

    Column {

        Text(
            text = title,
            style = MaterialTheme.typography.labelLarge
        )

        Spacer(
            modifier = Modifier.height(2.dp)
        )

        OutlinedTextField(
            value = value,

            onValueChange = onChange,

            modifier = Modifier
                .fillMaxWidth()
                .height(85.dp),

            textStyle = TextStyle(
                textAlign = TextAlign.Start,
                textDirection = TextDirection.ContentOrLtr
            )
        )
    }
}