package OpeningHours.domain.model

import OpeningHours.domain.manager.WorkingHoursDateFormatter
import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

data class WorkingHours(
        val open: Date,
        val close: Date
)

fun WorkingHours.getDisplayString(): String {
    val open = WorkingHoursDateFormatter.format(open)
    val close = WorkingHoursDateFormatter.format(close)
    return "$open - $close"
}

data class WorkingHoursNew(
        val open: Operation,
        val close: Operation
)

fun WorkingHoursNew.getDisplayString(): String {
    val openTime = WorkingHoursDateFormatter.format(open.time)
    val closeTime = WorkingHoursDateFormatter.format(close.time)
    val day = if (close.dayOfWeek == open.dayOfWeek || close.dayOfWeek.minus(1) == open.dayOfWeek) {
        ""
    } else {
        val dayOfWeekDisplayName = close.dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)
        " at $dayOfWeekDisplayName"
    }

    return "$openTime - $closeTime$day"
}

// todo - rename
data class Operation(
        val time: Date,
        val dayOfWeek: DayOfWeek
)

sealed class RestaurantWorkDetails {
    object Closed : RestaurantWorkDetails()
    object OpenWholeDay : RestaurantWorkDetails()
    data class Working(val workingHours: List<WorkingHoursNew>) : RestaurantWorkDetails()
}

fun RestaurantWorkDetails.Working.getDisplayString(): String =
        workingHours.joinToString(transform = WorkingHoursNew::getDisplayString)