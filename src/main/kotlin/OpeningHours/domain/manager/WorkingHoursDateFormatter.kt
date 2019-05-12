package OpeningHours.domain.manager

import java.text.SimpleDateFormat
import java.util.*

object WorkingHoursDateFormatter {
    private val hoursFormatter = getFormatter("hh")
    private val minutesFormatter = getFormatter("mm")
    private val secondsFormatter = getFormatter("ss")
    private val amPmFormatter = getFormatter("a")

    fun format(date: Date): String {
        val hours = hoursFormatter.format(date).toInt()
        val minutes = formatWithZeros(date, minutesFormatter)
        val seconds = formatWithZeros(date, secondsFormatter)
        val amPm = amPmFormatter.format(date)

        return buildString {
            append(hours)
            minutes?.let { append(".").append(it) }
            seconds?.let { append(":").append(it) }
            append(" ").append(amPm)
        }
    }

    private fun formatWithZeros(date: Date, formatter: SimpleDateFormat): String? =
            formatter.format(date).let {
                if (it.toInt() == 0) {
                    null
                } else {
                    it
                }
            }

    private fun getFormatter(pattern: String) = SimpleDateFormat(pattern).apply {
        timeZone = TimeZone.getTimeZone("UTC")
    }
}