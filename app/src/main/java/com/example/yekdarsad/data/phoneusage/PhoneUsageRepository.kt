package com.example.yekdarsad.data.phoneusage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.LocalDate
import java.time.ZoneId
import kotlinx.coroutines.runBlocking

class PhoneUsageRepository(
    private val context: Context,
    private val dao: PhoneUsageDao
) {

    suspend fun getScreenTime(
        date: String
    ): PhoneUsageData {


        val zone = ZoneId.of("Asia/Tehran")

        val selectedDate = LocalDate.parse(date)

        val todayDate = LocalDate.now(zone)

        println("PHONE USAGE DATE = $date")
        println("TODAY DATE = $todayDate")


        // اگر تاریخ آینده است
        if (selectedDate.isAfter(todayDate)) {

            return PhoneUsageData(
                screenTimeMinutes = 0,
                topApps = emptyList()
            )

        }


        val manager =
            context.getSystemService(
                Context.USAGE_STATS_SERVICE
            ) as UsageStatsManager




        val today = date



        val start =
            LocalDate.parse(date)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()



        val end =
            if (selectedDate == todayDate) {

                System.currentTimeMillis()

            } else {

                selectedDate
                    .plusDays(1)
                    .atStartOfDay(zone)
                    .toInstant()
                    .toEpochMilli()

            }



        val events =
            manager.queryEvents(
                start,
                end
            )



        val event =
            UsageEvents.Event()



        val openedApps =
            mutableMapOf<String, Long>()



        val appTimes =
            mutableMapOf<String, Long>()




        while(events.hasNextEvent()) {


            events.getNextEvent(event)



            val pkg =
                event.packageName
                    ?: continue




            when(event.eventType) {


                UsageEvents.Event.MOVE_TO_FOREGROUND,
                UsageEvents.Event.ACTIVITY_RESUMED -> {


                    openedApps[pkg] =
                        event.timeStamp


                }



                UsageEvents.Event.MOVE_TO_BACKGROUND,
                UsageEvents.Event.ACTIVITY_PAUSED -> {


                    val opened =
                        openedApps.remove(pkg)



                    if(opened != null) {


                        val duration =
                            event.timeStamp - opened



                        if(duration > 0) {


                            appTimes[pkg] =
                                (appTimes[pkg] ?: 0L) + duration


                        }


                    }

                }

            }

        }





        openedApps.forEach { (pkg, opened) ->


            appTimes[pkg] =
                (appTimes[pkg] ?: 0L) +
                        (end - opened)


        }





        println("========== FINAL APPS ==========")



        val apps =

            appTimes

                .mapNotNull { (pkg, millis) ->



                    val minutes =
                        millis / 1000 / 60



                    if(minutes <= 0)
                        return@mapNotNull null





                    val name =
                        getAppName(pkg)



                    println(
                        "FINAL APP $name = $minutes"
                    )



                    AppUsage(

                        packageName = pkg,

                        appName = name,

                        minutes = minutes

                    )


                }

                .sortedByDescending {

                    it.minutes

                }





        apps.take(10).forEach {


            println(
                "SHOW APP ${it.appName} = ${it.minutes}"
            )


        }





        val total =

            appTimes.values.sumOf {

                it / 1000 / 60

            }





        println(
            "FINAL SCREEN TIME = $total"
        )





        // ذخیره در Room

        // ذخیره روزانه در Room

        runBlocking {

            dao.insertTotal(

                PhoneUsageEntity(

                    date = today,

                    totalMinutes = total

                )

            )


            dao.deleteApps(today)



            dao.insertApps(

                apps.take(5).map {

                    PhoneAppUsageEntity(

                        date = today,

                        packageName = it.packageName,

                        appName = it.appName,

                        minutes = it.minutes

                    )

                }

            )

        }





        return PhoneUsageData(

            screenTimeMinutes = total,

            topApps = apps.take(5)

        )


    }








    private fun getAppName(
        packageName: String
    ): String {


        return when(packageName) {


            "com.instagram.android" ->
                "Instagram"



            "org.telegram.messenger" ->
                "Telegram"



            "com.android.chrome" ->
                "Chrome"



            "com.google.android.apps.messaging" ->
                "Messages"



            "com.openai.chatgpt" ->
                "ChatGPT"



            "ir.nasim" ->
                "Bale"



            "com.example.yekdarsad" ->
                "یک درصد"



            else -> {


                try {


                    val info =
                        context.packageManager
                            .getApplicationInfo(
                                packageName,
                                0
                            )



                    val label =
                        context.packageManager
                            .getApplicationLabel(info)
                            .toString()



                    if(label.isBlank())
                        packageName
                    else
                        label



                } catch(e: Exception) {


                    packageName


                }


            }


        }


    }

}