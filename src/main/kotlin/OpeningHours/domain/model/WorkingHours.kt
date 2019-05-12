package OpeningHours.domain.model

import OpeningHours.domain.manager.WorkingHoursDateFormatter
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