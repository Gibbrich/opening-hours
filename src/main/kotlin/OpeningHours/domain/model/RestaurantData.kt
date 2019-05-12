package OpeningHours.domain.model

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

data class RestaurantData(
        val workingHours: Map<DayOfWeek, List<WorkingHours>>
)

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