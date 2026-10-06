package com.example.yekdarsad.data.phoneusage

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import java.time.LocalDate
import java.time.ZoneId

class PhoneUsageEventsRepository(
    private val context: Context
) {

    fun getTodayScreenTime(): PhoneUsageData {

        val manager =
            context.getSystemService(
                Context.USAGE_STATS_SERVICE
            ) as UsageStatsManager

        val zone = ZoneId.systemDefault()

        val start =
            LocalDate.now(zone)
                .atStartOfDay(zone)
                .toInstant()
                .toEpochMilli()

        val end =
            System.currentTimeMillis()

        val events =
            manager.queryEvents(
                start,
                end
            )

        val foregroundMap =
            mutableMapOf<String, Long>()

        val totalMap =
            mutableMapOf<String, Long>()

        val event =
            UsageEvents.Event()

        while (events.hasNextEvent()) {

            events.getNextEvent(event)

            val pkg =
                event.packageName
                    ?: continue

            when (event.eventType) {

                UsageEvents.Event.ACTIVITY_RESUMED,
                UsageEvents.Event.MOVE_TO_FOREGROUND -> {

                    foregroundMap[pkg] =
                        event.timeStamp
                }

                UsageEvents.Event.ACTIVITY_PAUSED,
                UsageEvents.Event.MOVE_TO_BACKGROUND -> {

                    val begin =
                        foregroundMap.remove(pkg)
                            ?: continue

                    val diff =
                        event.timeStamp - begin

                    if (diff > 0) {

                        totalMap[pkg] =
                            (totalMap[pkg] ?: 0L) + diff
                    }
                }
            }
        }

        // اپ‌هایی که هنوز باز هستند
        foregroundMap.forEach { (pkg, begin) ->

            val diff = end - begin

            if (diff > 0) {

                totalMap[pkg] =
                    (totalMap[pkg] ?: 0L) + diff
            }
        }

        val apps =
            totalMap
                .mapNotNull { (pkg, millis) ->

                    val minutes =
                        millis / 1000 / 60

                    if (minutes <= 0)
                        return@mapNotNull null

                    val name =
                        try {

                            val info =
                                context.packageManager
                                    .getApplicationInfo(
                                        pkg,
                                        0
                                    )

                            context.packageManager
                                .getApplicationLabel(info)
                                .toString()

                        } catch (e: Exception) {

                            pkg
                        }

                    AppUsage(
                        packageName = pkg,
                        appName = name,
                        minutes = minutes
                    )
                }
                .sortedByDescending {
                    it.minutes
                }

        val total =
            apps.sumOf {
                it.minutes
            }

        println("EVENT TOTAL = $total")

        apps.take(15).forEach {

            println(
                "EVENT APP ${it.packageName} = ${it.minutes}"
            )
        }

        return PhoneUsageData(
            screenTimeMinutes = total,
            topApps = apps.take(5)
        )
    }
}