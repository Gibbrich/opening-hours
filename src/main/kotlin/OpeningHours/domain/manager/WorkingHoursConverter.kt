package OpeningHours.domain.manager

import OpeningHours.data.*
import OpeningHours.domain.model.*
import OpeningHours.domain.utils.ConvertException
import OpeningHours.domain.utils.range
import OpeningHours.domain.utils.values
import java.time.DayOfWeek
import java.util.*

private typealias ExtWorkingOperationTime = Pair<ExtOpeningHour, DayOfWeek>

object WorkingHoursConverter {
    const val MIN_OPEN_TIME = 0 // 00:00:00
    const val MAX_CLOSE_TIME = 60 * 60 * 24 - 1 // 23:59:59

    const val NO_CLOSE_HOUR = "Close hour might be the last operation in current day working hours, if restaurant closes the same day, or first operation, if restaurant closes in one of the next days."

    fun getRestaurantData(extRestaurantData: ExtRestaurantData): RestaurantData {
        var currentDayId = 0
        val result = mutableMapOf<DayOfWeek, WorkingState>()
        while (currentDayId <= DayOfWeek.values().lastIndex) {
            val currentDay = DayOfWeek.of(currentDayId + 1)
            val openingHours = extRestaurantData.getOpeningHours(currentDay)

            val isOpeningHoursContainOnlyClosePreviousDayOperation = openingHours.size == 1 && openingHours[0].type == ExtType.CLOSE

            if (openingHours.isEmpty() || isOpeningHoursContainOnlyClosePreviousDayOperation) {
                result[currentDay] = WorkingState.Closed
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

            val workingHoursList = mutableListOf<WorkingHours>()

            for (i in startIndex..openingHours.lastIndex step 2) {
                val open = ExtWorkingOperationTime(openingHours[i], currentDay)

                val potentialCloseHourId: Int = i + 1
                val close = if (potentialCloseHourId <= openingHours.lastIndex) {
                    ExtWorkingOperationTime(openingHours[potentialCloseHourId], currentDay)
                } else {
                    getClosingHour(extRestaurantData, currentDay)
                }

                val workingHours = getWorkingHours(open, close)

                workingHoursList.add(workingHours)
            }

            result[currentDay] = WorkingState.Working(workingHoursList)

            val closeDay = workingHoursList.last().close.dayOfWeek

            if (closeDay == currentDay || closeDay == currentDay.plus(1)) {
                currentDayId++
            } else {
                for (day in currentDay.plus(1).range(closeDay)) {
                    result[day] = WorkingState.OpenedWholeDay
                }

                if (closeDay < currentDay) {
                    break
                } else {
                    currentDayId = closeDay.ordinal
                }
            }
        }

        return RestaurantData(result)
    }

    fun getRestaurantDataNew(extRestaurantData: ExtRestaurantData): RestaurantData {
        var currentDay = getFirstDayWithNonEmptyWorkingHours(extRestaurantData) ?: return RestaurantData(emptyMap())

        val result = mutableMapOf<DayOfWeek, WorkingState>()
        while (isAllDaysFilled(result).not()) {
            if (currentDay in result) {
                currentDay = currentDay.plus(1)
                continue
            }

            val openingHours = extRestaurantData.getOpeningHours(currentDay)

            if (openingHours.isEmpty()) {
                result[currentDay] = WorkingState.Closed
                currentDay = currentDay.plus(1)
                continue
            }

            if (openingHours.size == 1 && openingHours[0].type == ExtType.CLOSE) {
                val workingHours = getWorkingHours(
                        ExtWorkingOperationTime(
                                ExtOpeningHour(ExtType.OPEN, 0),
                                currentDay
                        ),
                        ExtWorkingOperationTime(
                                openingHours[0],
                                currentDay
                        )
                )
                result[currentDay] = WorkingState.Working(listOf(workingHours))
                currentDay = currentDay.plus(1)
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

            val workingHoursList = mutableListOf<WorkingHours>()

            for (i in startIndex..openingHours.lastIndex step 2) {
                val open = ExtWorkingOperationTime(openingHours[i], currentDay)

                val potentialCloseHourId: Int = i + 1
                var close = if (potentialCloseHourId <= openingHours.lastIndex) {
                    ExtWorkingOperationTime(openingHours[potentialCloseHourId], currentDay)
                } else {
                    null
                }

                if (close == null) {
                    var closingDay = currentDay.plus(1)
                    loop1@ for (hours in extRestaurantData.toIteratorWrapper(closingDay)) {
                        when {
                            hours.isEmpty() -> {
                                result[closingDay] = WorkingState.OpenedWholeDay
                                closingDay = closingDay.plus(1)
                            }

                            hours.first().type == ExtType.CLOSE -> {
                                close = ExtWorkingOperationTime(hours.first(), closingDay)
                                break@loop1
                            }
                            else -> {
                                throw ConvertException(NO_CLOSE_HOUR)
                            }
                        }
                    }
                }

                close ?: throw ConvertException(NO_CLOSE_HOUR)

                val workingHours = getWorkingHours(open, close)
                workingHoursList.add(workingHours)
            }

            result[currentDay] = WorkingState.Working(workingHoursList)

            currentDay = currentDay.plus(1)
        }

        return RestaurantData(result)
    }

    private fun isAllDaysFilled(map: Map<DayOfWeek, WorkingState>): Boolean = DayOfWeek
            .values()
            .all(map::containsKey)

    private fun getFirstDayWithNonEmptyWorkingHours(
            extRestaurantData: ExtRestaurantData
    ): DayOfWeek? = DayOfWeek
            .values()
            .firstOrNull {
                extRestaurantData.getOpeningHours(it).isNotEmpty()
            }

    /**
     * If last operation type in current day is ExtType.OPEN, it means,
     * that restaurant closes at the next day. It is possible, that restaurant will
     * work more than 24h, so theoretically it can open on Monday at 00:00 and close on Sunday at 23:59:59.
     * Unfortunately, I didn't receive more precise limitations, so implementation
     * designed to handle described above corner case.
     */
    private fun getClosingHour(
            extRestaurantData: ExtRestaurantData,
            currentDay: DayOfWeek
    ): ExtWorkingOperationTime {
        var closingDay = currentDay.plus(1)
        for (hours in extRestaurantData.toIteratorWrapper(closingDay)) {
            when {
                hours.isEmpty() -> {
                    closingDay = closingDay.plus(1)
                }

                hours.first().type == ExtType.CLOSE -> {
                    return ExtWorkingOperationTime(hours.first(), closingDay)
                }
                else -> {
                    throw ConvertException(NO_CLOSE_HOUR)
                }
            }
        }

        throw ConvertException(NO_CLOSE_HOUR)
    }

    private fun getWorkingHours(
            open: ExtWorkingOperationTime,
            close: ExtWorkingOperationTime
    ): WorkingHours = when {
        open.first.type != ExtType.OPEN || close.first.type != ExtType.CLOSE -> {
            throw ConvertException("Incorrect ordering of working hours: open = ${open.first}, close = ${close.first}")
        }

        open.first.value < MIN_OPEN_TIME || close.first.value > MAX_CLOSE_TIME -> {
            throw ConvertException("Open and close time must be in [$MIN_OPEN_TIME; $MAX_CLOSE_TIME]. Open: $open, close: $close")
        }

        else -> {
            WorkingHours(
                    WorkingOperationTime(Date(open.first.value * 1000), open.second),
                    WorkingOperationTime(Date(close.first.value * 1000), close.second)
            )
        }
    }
}