package OpeningHours.domain.manager

import OpeningHours.data.*
import OpeningHours.domain.minus
import OpeningHours.domain.model.*
import OpeningHours.domain.range
import java.time.DayOfWeek
import java.util.*

object WorkingHoursConverter {
    const val MIN_OPEN_TIME = 0 // 00:00:00
    const val MAX_CLOSE_TIME = 60 * 60 * 24 - 1 // 23:59:59

    // todo - change text
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

    fun getRestaurantDataNew(extRestaurantData: ExtRestaurantData): RestaurantDataNew {
        var currentDayId = 0
        val result = mutableMapOf<DayOfWeek, RestaurantWorkDetails>()
        while (currentDayId <= DayOfWeek.values().lastIndex) {
            val currentDay = DayOfWeek.of(currentDayId + 1)
            val openingHours = extRestaurantData.getOpeningHours(currentDay)

            val isOpeningHoursContainOnlyClosePreviousDayOperation = openingHours.size == 1 && openingHours[0].type == ExtType.CLOSE

            if (openingHours.isEmpty() || isOpeningHoursContainOnlyClosePreviousDayOperation) {
                result[currentDay] = RestaurantWorkDetails.Closed
                currentDayId++
                continue
            }

            // if currentDayOpeningHours starts with ExtType.CLOSE,
            // it means, that previous working day was closed today, so we need
            // start with the next one
            val startIndex = if (openingHours.first().type == ExtType.OPEN) {
                0
            } else {
                1
            }

            val workingHoursList = mutableListOf<WorkingHoursNew>()

            for (i in startIndex..openingHours.lastIndex step 2) {
                val open = openingHours[i] to currentDay
                val potentialCloseHourId: Int = i + 1
                val close = if (potentialCloseHourId <= openingHours.lastIndex) {
                    openingHours[potentialCloseHourId] to currentDay
                } else {
                    /**
                     * if last operation type in current day is ExtType.OPEN, it means,
                     * that restaurant closes at the next day. It is possible, that restaurant will
                     * work more than 24h, so theoretically it can open on Monday at 00:00 and close on Sunday at 23:59:59.
                     * Unfortunately, I didn't receive more precise limitations, so implementation
                     * designed to handle described above corner case.
                     */
                    getClosingHour(extRestaurantData, currentDay)
                }

                val workingHours = getWorkingHoursNew(open, close)

                workingHoursList.add(workingHours)
            }

            result[currentDay] = RestaurantWorkDetails.Working(workingHoursList)

            val closeDay = workingHoursList.last().close.dayOfWeek

            if (closeDay == currentDay || closeDay == currentDay.plus(1)) {
                currentDayId++
            } else {
                for (day in currentDay.plus(1).range(closeDay)) {
                    result[day] = RestaurantWorkDetails.OpenWholeDay
                }

                if (closeDay < currentDay) {
                    break
                } else {
                    currentDayId = closeDay.ordinal
                }
            }
        }

        return RestaurantDataNew(result)
    }


    private fun getClosingHour(
            extRestaurantData: ExtRestaurantData,
            currentDay: DayOfWeek
    ): Pair<ExtOpeningHour, DayOfWeek> {
        var closingDay = currentDay.plus(1)
        for (hours in extRestaurantData.toIteratorWrapper(closingDay)) {
            when {
                hours.isEmpty() -> {
                    closingDay = closingDay.plus(1)
                }

                hours.first().type == ExtType.CLOSE -> {
                    return hours.first() to closingDay
                }
                else -> {
                    throw IllegalArgumentException() // todo - add description
                }
            }
        }

        throw IllegalArgumentException(NO_CLOSE_HOUR)
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

    private fun getWorkingHoursNew(
            open: Pair<ExtOpeningHour, DayOfWeek>,
            close: Pair<ExtOpeningHour, DayOfWeek>
    ): WorkingHoursNew = when {
        open.first.type != ExtType.OPEN || close.first.type != ExtType.CLOSE -> {
            throw IllegalArgumentException("Incorrect ordering of working hours: open = ${open.first}, close = ${close.first}")
        }

        open.first.value < MIN_OPEN_TIME || open.first.value > MAX_CLOSE_TIME -> {
            throw IllegalArgumentException("Open and close time must be in [$MIN_OPEN_TIME; $MAX_CLOSE_TIME]. Open: $open, close: $close")
        }

        else -> {
            WorkingHoursNew(
                    Operation(Date(open.first.value * 1000), open.second),
                    Operation(Date(close.first.value * 1000), close.second)
            )
        }
    }

}