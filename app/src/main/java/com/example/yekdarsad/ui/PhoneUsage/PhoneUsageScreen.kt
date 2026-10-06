package com.example.yekdarsad.ui.phoneusage

import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.yekdarsad.R
import com.example.yekdarsad.data.phoneusage.AppUsage
import com.example.yekdarsad.viewmodel.PhoneUsageViewModel
import java.time.LocalDate

private val YekDarsadPrimary =
    Color(0xFF6C63FF)


private val GlassColor =
    Color.White.copy(alpha = 0.06f)



@Composable
fun PhoneUsageScreen(
phoneUsageViewModel: PhoneUsageViewModel,
date: String
) {


    val context = LocalContext.current


    val data by phoneUsageViewModel
        .phoneUsage
        .collectAsState()

    println(
        "PHONE SCREEN DATE=$date DATA=${data?.screenTimeMinutes}"
    )



    LaunchedEffect(date) {

        phoneUsageViewModel.refresh(
            context,
            date
        )

    }



    var visible by remember {

        mutableStateOf(false)

    }



    LaunchedEffect(Unit) {

        visible = true

    }



    AnimatedVisibility(

        visible = visible,

        enter =
            fadeIn(
                tween(500)
            )
                    +
                    slideInVertically(
                        tween(
                            500,
                            easing = FastOutSlowInEasing
                        )
                    ){

                        it / 4

                    }

    ){



        LazyColumn(

            modifier = Modifier
                .fillMaxSize()
                .padding(14.dp),

            verticalArrangement =
                Arrangement.spacedBy(12.dp)

        ){



            item {


                ScreenTimeCard(

                    data?.screenTimeMinutes ?: 0

                )


            }



            item {


                Text(

                    text = "۵ برنامه پرمصرف",

                    style =
                        MaterialTheme.typography.titleMedium,

                    fontWeight =
                        FontWeight.Bold

                )


            }




            items(

                data?.topApps ?: emptyList()

            ){ app ->



                AppUsageCard(

                    app

                )


            }



        }



    }


}

@Composable
private fun ScreenTimeCard(
    totalMinutes: Long
) {


    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(88.dp)
            .border(
                1.dp,
                Color.White.copy(alpha = .12f),
                RoundedCornerShape(18.dp)
            ),

        shape = RoundedCornerShape(18.dp),

        colors = CardDefaults.cardColors(

            containerColor = GlassColor

        ),

        elevation = CardDefaults.cardElevation(

            defaultElevation = 0.dp

        )

    ){



        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 14.dp),

            verticalAlignment = Alignment.CenterVertically

        ){



            Box(

                modifier = Modifier
                    .size(44.dp)
                    .clip(CircleShape)
                    .background(
                        YekDarsadPrimary.copy(
                            alpha = .16f
                        )
                    ),

                contentAlignment = Alignment.Center

            ){



                Icon(

                    imageVector = Icons.Default.PhoneAndroid,

                    contentDescription = null,

                    tint = YekDarsadPrimary,

                    modifier = Modifier.size(22.dp)

                )



            }



            Spacer(

                modifier = Modifier.width(12.dp)

            )



            Column {



                Text(

                    text = "مصرف امروز گوشی",

                    style =
                        MaterialTheme.typography.bodyMedium,

                    fontWeight =
                        FontWeight.SemiBold

                )



                Text(

                    text = formatMinutes(totalMinutes),

                    style =
                        MaterialTheme.typography.titleLarge,

                    color = YekDarsadPrimary,

                    fontWeight =
                        FontWeight.ExtraBold

                )



            }




        }



    }


}





@Composable
private fun AppUsageCard(
    app: AppUsage
) {


    val appColor =
        getAppColor(
            app.packageName
        )



    Card(

        modifier = Modifier
            .fillMaxWidth()
            .height(70.dp)
            .border(

                1.dp,

                Color.White.copy(alpha = .08f),

                RoundedCornerShape(16.dp)

            ),

        shape = RoundedCornerShape(16.dp),


        colors = CardDefaults.cardColors(

            containerColor = GlassColor

        ),


        elevation = CardDefaults.cardElevation(

            defaultElevation = 0.dp

        )


    ){



        Row(

            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp),


            verticalAlignment = Alignment.CenterVertically

        ){



            Box(

                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(

                        appColor.copy(
                            alpha = .14f
                        )

                    ),


                contentAlignment = Alignment.Center

            ){



                AppIcon(

                    packageName = app.packageName

                )



            }



            Spacer(

                Modifier.width(12.dp)

            )



            Column(

                modifier = Modifier.weight(1f)

            ){



                Text(

                    text = app.appName,

                    style =
                        MaterialTheme.typography.bodyLarge,


                    fontWeight =
                        FontWeight.SemiBold,


                    maxLines = 1,


                    overflow =
                        TextOverflow.Ellipsis

                )



                Text(

                    text =
                        formatMinutes(
                            app.minutes
                        ),


                    style =
                        MaterialTheme.typography.bodySmall,


                    color =
                        MaterialTheme.colorScheme
                            .onSurfaceVariant

                )



            }




        }



    }


}





private fun getAppColor(
    packageName: String
): Color {


    return when(packageName){


        "com.instagram.android" ->
            Color(0xFFE1306C)


        "org.telegram.messenger" ->
            Color(0xFF229ED9)


        "com.android.chrome" ->
            Color(0xFFF4B400)


        "com.google.android.youtube" ->
            Color(0xFFFF0000)


        "com.whatsapp" ->
            Color(0xFF25D366)


        "com.spotify.music" ->
            Color(0xFF1DB954)


        else ->
            YekDarsadPrimary


    }


}

private fun formatMinutes(
    minutes: Long
): String {


    val hours =
        minutes / 60L


    val mins =
        minutes % 60L



    return when {


        hours == 0L ->

            "$mins دقیقه"



        mins == 0L ->

            "$hours ساعت"



        else ->

            "$hours ساعت و $mins دقیقه"


    }


}




@Composable
private fun AppIcon(
    packageName: String
) {


    val context =
        LocalContext.current



    when(packageName){



        "com.instagram.android" -> {



            Image(

                painter =
                    painterResource(
                        R.drawable.ic_instagram
                    ),

                contentDescription = null,

                modifier =
                    Modifier.size(32.dp)

            )


        }




        "org.telegram.messenger" -> {



            Image(

                painter =
                    painterResource(
                        R.drawable.ic_telegram
                    ),

                contentDescription = null,

                modifier =
                    Modifier.size(32.dp)

            )


        }





        "com.android.chrome" -> {



            Image(

                painter =
                    painterResource(
                        R.drawable.ic_chrome
                    ),

                contentDescription = null,

                modifier =
                    Modifier.size(32.dp)

            )


        }





        else -> {



            val bitmap =
                remember(packageName){



                    try {



                        val drawable =
                            context.packageManager
                                .getApplicationIcon(
                                    packageName
                                )



                        val bitmap =
                            Bitmap.createBitmap(

                                72,

                                72,

                                Bitmap.Config.ARGB_8888

                            )



                        val canvas =
                            Canvas(bitmap)



                        drawable.setBounds(

                            0,

                            0,

                            canvas.width,

                            canvas.height

                        )



                        drawable.draw(canvas)



                        bitmap



                    } catch(e: Exception){


                        null


                    }



                }





            if(bitmap != null){



                Image(

                    bitmap =
                        bitmap.asImageBitmap(),


                    contentDescription = null,


                    modifier =
                        Modifier.size(32.dp)


                )



            } else {



                Icon(

                    imageVector =
                        Icons.Default.PhoneAndroid,


                    contentDescription = null,


                    modifier =
                        Modifier.size(32.dp),


                    tint =
                        YekDarsadPrimary


                )



            }





        }





    }




}