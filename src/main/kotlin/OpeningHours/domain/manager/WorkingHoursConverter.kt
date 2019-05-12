package OpeningHours.domain.manager

import OpeningHours.data.ExtType
import OpeningHours.data.ExtOpeningHour
import OpeningHours.data.ExtRestaurantData
import OpeningHours.domain.model.RestaurantData
import OpeningHours.domain.model.WorkingHours
import java.time.DayOfWeek
import java.util.*

object WorkingHoursConverter {
    const val MIN_OPEN_TIME = 0 // 00:00:00
    const val MAX_CLOSE_TIME = 60 * 60 * 24 - 1 // 23:59:59

    const val NO_CLOSE_HOUR = "Close hour might be either in currentDayOpeningHours, if restaurant closes the same day, or nextDayCloseHour, if restaurant closes next day."

    fun getWorkingHours(data: ExtRestaurantData): RestaurantData {
        val workingHours = mapOf(
                DayOfWeek.MONDAY to getWorkingHours(data.monday, data.tuesday),
                DayOfWeek.TUESDAY to getWorkingHours(data.tuesday, data.wednesday),
                DayOfWeek.WEDNESDAY to getWorkingHours(data.wednesday, data.thursday),
                DayOfWeek.THURSDAY to getWorkingHours(data.thursday, data.friday),
                DayOfWeek.FRIDAY to getWorkingHours(data.friday, data.saturday),
                DayOfWeek.SATURDAY to getWorkingHours(data.saturday, data.sunday),
                DayOfWeek.SUNDAY to getWorkingHours(data.sunday, data.monday)
        )

        return RestaurantData(workingHours)
    }

    private fun getWorkingHours(
            currentDayOpeningHours: List<ExtOpeningHour>,
            nextDayOpeningHours: List<ExtOpeningHour>
    ): List<WorkingHours> {
        val nextDayCloseHour = if (nextDayOpeningHours.isNotEmpty() && nextDayOpeningHours[0].type == ExtType.CLOSE) {
            nextDayOpeningHours[0]
        } else {
            null
        }

        return getWorkingHours(currentDayOpeningHours, nextDayCloseHour)
    }

    private fun getWorkingHours(
            currentDayOpeningHours: List<ExtOpeningHour>,
            nextDayCloseHour: ExtOpeningHour?
    ): List<WorkingHours> {
        val isDataContainOnlyClosePreviousDayOperation = currentDayOpeningHours.size == 1 && currentDayOpeningHours[0].type == ExtType.CLOSE
        if (currentDayOpeningHours.isEmpty() || isDataContainOnlyClosePreviousDayOperation) {
            return emptyList()
        }

        // if currentDayOpeningHours starts with ExtType.CLOSE,
        // it means, that previous working day was closed today, so we need
        // start with the next one
        val startIndex = if (currentDayOpeningHours[0].type == ExtType.OPEN) {
            0
        } else {
            1
        }

        val result = mutableListOf<WorkingHours>()

        for (i in startIndex..currentDayOpeningHours.lastIndex step 2) {
            val open = currentDayOpeningHours[i]
            val close = getCloseHour(i + 1, currentDayOpeningHours, nextDayCloseHour)
            val workingHours = getWorkingHours(open, close)

            result.add(workingHours)
        }

        return result
    }

    private fun getCloseHour(
            potentialCloseHourId: Int,
            currentDayOpeningHours: List<ExtOpeningHour>,
            nextDayCloseHour: ExtOpeningHour?
    ) = when {
        potentialCloseHourId <= currentDayOpeningHours.lastIndex -> {
            currentDayOpeningHours[potentialCloseHourId]
        }

        nextDayCloseHour != null -> {
            nextDayCloseHour
        }

        else -> {
            throw IllegalArgumentException(NO_CLOSE_HOUR)
        }
    }

    private fun getWorkingHours(
            open: ExtOpeningHour,
            close: ExtOpeningHour
    ): WorkingHours = when {
        open.type != ExtType.OPEN || close.type != ExtType.CLOSE -> {
            throw IllegalArgumentException("Incorrect ordering of working hours: open = $open, close = $close")
        }

        open.value < MIN_OPEN_TIME || close.value > MAX_CLOSE_TIME -> {
            throw IllegalArgumentException("Open and close time must be in [$MIN_OPEN_TIME; $MAX_CLOSE_TIME]. Open: $open, close: $close")
        }

        else -> {
            WorkingHours(
                    Date(open.value * 1000),
                    Date(close.value * 1000)
            )
        }
    }
}