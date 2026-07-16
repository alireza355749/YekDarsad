package com.example.yekdarsad

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.example.yekdarsad.ui.theme.YekDarsadTheme
import java.time.LocalDate


class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {

            YekDarsadTheme {

                MainScreen()

            }

        }
    }
}



@Composable
fun MainScreen() {


    var currentScreen by remember {
        mutableStateOf<Screen>(Screen.Home)
    }


    var selectedCategory by remember {
        mutableStateOf("")
    }


    var selectedCategoryId by remember {
        mutableStateOf(0)
    }

    var selectedDate by remember {
        mutableStateOf(LocalDate.now().toString())
    }




    Scaffold(

        bottomBar = {


            NavigationBar(

                containerColor = Color(0xFFFFFBEA)

            ) {



                NavigationBarItem(

                    selected = currentScreen == Screen.Home,

                    onClick = {
                        currentScreen = Screen.Home
                    },

                    icon = {
                        Icon(
                            Icons.Default.Home,
                            null
                        )
                    },

                    label = {
                        Text("خانه")
                    },

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor = Color(0xFF7A9A6D),

                        selectedTextColor = Color(0xFF7A9A6D),

                        indicatorColor = Color(0xFFA8C3A0)

                    )

                )






                NavigationBarItem(

                    selected = currentScreen == Screen.Today,

                    onClick = {
                        currentScreen = Screen.Today
                    },

                    icon = {
                        Icon(
                            Icons.Default.Today,
                            null
                        )
                    },

                    label = {
                        Text("امروز")
                    },

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor = Color(0xFF7A9A6D),

                        selectedTextColor = Color(0xFF7A9A6D),

                        indicatorColor = Color(0xFFA8C3A0)

                    )

                )







                NavigationBarItem(

                    selected = currentScreen == Screen.Statistics,

                    onClick = {
                        currentScreen = Screen.Statistics
                    },

                    icon = {
                        Icon(
                            Icons.Default.BarChart,
                            null
                        )
                    },

                    label = {
                        Text("آمار")
                    },

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor = Color(0xFF7A9A6D),

                        selectedTextColor = Color(0xFF7A9A6D),

                        indicatorColor = Color(0xFFA8C3A0)

                    )

                )








                NavigationBarItem(

                    selected = currentScreen == Screen.Activities,

                    onClick = {
                        currentScreen = Screen.Activities
                    },

                    icon = {
                        Icon(
                            Icons.Default.List,
                            null
                        )
                    },

                    label = {
                        Text("برنامه ریزی")
                    },

                    colors = NavigationBarItemDefaults.colors(

                        selectedIconColor = Color(0xFF7A9A6D),

                        selectedTextColor = Color(0xFF7A9A6D),

                        indicatorColor = Color(0xFFA8C3A0)

                    )

                )



            }


        }


    ) { padding ->



        Surface(

            modifier = Modifier.padding(padding)

        ) {



            when (currentScreen) {



                Screen.Home -> HomeScreen()



                Screen.Today -> TodayScreen()



                Screen.Statistics -> StatisticsScreen()



                Screen.Activities -> ActivitiesScreen(

                    onCategoryClick = { id, name, date ->

                        selectedCategoryId = id

                        selectedCategory = name

                        selectedDate = date

                        currentScreen = Screen.CategoryDetail

                    }

                )

                Screen.CategoryDetail -> CategoryDetailScreen(

                    categoryId = selectedCategoryId,

                    categoryName = selectedCategory,

                    selectedDate = selectedDate

                )


            }


        }


    }


}