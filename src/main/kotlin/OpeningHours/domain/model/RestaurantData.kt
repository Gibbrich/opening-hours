package OpeningHours.domain.model

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

data class RestaurantData(
        val workingHours: Map<DayOfWeek, List<WorkingHours>>
)

data class RestaurantDataNew(
        val workingHours: Map<DayOfWeek, RestaurantWorkDetails>
)

fun RestaurantDataNew.getDisplayString(): String =
        DayOfWeek.values().joinToString("\n", transform = ::getDisplayString)

private fun RestaurantDataNew.getDisplayString(dayOfWeek: DayOfWeek): String {
    val hours = workingHours[dayOfWeek]
    val dayOfWeekDisplayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    val workingHoursDisplayString = when (hours) {
        RestaurantWorkDetails.Closed -> "Closed"
        RestaurantWorkDetails.OpenWholeDay -> "Opened whole day"
        is RestaurantWorkDetails.Working -> hours.getDisplayString()
        null -> "Closed"
    }

    return "$dayOfWeekDisplayName: $workingHoursDisplayString"
}

fun RestaurantData.getDisplayString(): String =
        DayOfWeek.values().joinToString("\n", transform = ::getDisplayString)

private fun RestaurantData.getDisplayString(dayOfWeek: DayOfWeek): String {
    val hours = workingHours[dayOfWeek]
    val dayOfWeekDisplayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    val workingHoursDisplayString = if (hours.isNullOrEmpty()) {
        "Closed"
    } else {
        hours.joinToString(", ", transform = WorkingHours::getDisplayString)
    }

    return "$dayOfWeekDisplayName: $workingHoursDisplayString"
}