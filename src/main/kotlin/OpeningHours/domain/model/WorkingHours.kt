package OpeningHours.domain.model

import OpeningHours.domain.manager.WorkingHoursDateFormatter
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

data class WorkingHours(
        val open: WorkingOperationTime,
        val close: WorkingOperationTime
)

fun WorkingHours.getDisplayString(): String {
    val openTime = WorkingHoursDateFormatter.format(open.time)
    val closeTime = WorkingHoursDateFormatter.format(close.time)
    val dayOfWeekDisplayName = close.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
    val closeDay = when {
        close.dayOfWeek == open.dayOfWeek -> {
            if (close.time < open.time) {
                " at next $dayOfWeekDisplayName"
            } else {
                ""
            }
        }

        close.dayOfWeek.minus(1) == open.dayOfWeek -> {
            ""
        }

        else -> {
            " at $dayOfWeekDisplayName"
        }
    }

    return "$openTime - $closeTime$closeDay"
}

data class WorkingOperationTime(
        val time: Date,
        val dayOfWeek: DayOfWeek
)