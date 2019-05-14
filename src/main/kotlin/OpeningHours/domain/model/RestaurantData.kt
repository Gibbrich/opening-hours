package OpeningHours.domain.model

import java.time.DayOfWeek
import java.time.format.TextStyle
import java.util.*

data class RestaurantData(
        val workingHours: Map<DayOfWeek, WorkingState>
)

sealed class WorkingState {
    object Closed : WorkingState()
    object OpenedWholeDay : WorkingState()
    data class Working(val workingHours: List<WorkingHours>) : WorkingState()
}

fun RestaurantData.getDisplayString(): String =
        DayOfWeek.values().joinToString("\n", transform = ::getDisplayString)

private fun RestaurantData.getDisplayString(dayOfWeek: DayOfWeek): String {
    val hours = workingHours[dayOfWeek]
    val dayOfWeekDisplayName = dayOfWeek.getDisplayName(TextStyle.FULL, Locale.ENGLISH)

    val workingStateDisplayString = when (hours) {
        WorkingState.Closed -> "Closed"
        WorkingState.OpenedWholeDay -> "Opened whole day"
        is WorkingState.Working -> hours.getDisplayString()
        null -> "Closed"
    }

    return "$dayOfWeekDisplayName: $workingStateDisplayString"
}

fun WorkingState.Working.getDisplayString(): String =
        workingHours.joinToString(transform = WorkingHours::getDisplayString)