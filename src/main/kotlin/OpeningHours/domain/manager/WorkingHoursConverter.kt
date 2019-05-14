package OpeningHours.domain.manager

import OpeningHours.data.*
import OpeningHours.domain.model.*
import OpeningHours.domain.utils.ConvertException
import OpeningHours.domain.utils.toSingletonList
import java.time.DayOfWeek
import java.util.*

private typealias ExtWorkingOperationTime = Pair<ExtOpeningHour, DayOfWeek>

object WorkingHoursConverter {
    const val MIN_OPEN_TIME = 0 // 00:00:00
    const val MAX_CLOSE_TIME = 60 * 60 * 24 - 1 // 23:59:59

    const val NO_CLOSE_HOUR = "Close hour might be the last operation in current day working hours, if restaurant closes the same day, or first operation, if restaurant closes in one of the next days."

    /**
     * Converts [ExtRestaurantData] to [RestaurantData]
     *
     * @throws ConvertException if input data is somehow incorrect
     */
    fun getRestaurantData(extRestaurantData: ExtRestaurantData): RestaurantData {
        var currentDay = getFirstDayWithNonEmptyWorkingHours(extRestaurantData) ?: return RestaurantData(emptyMap())

        val result = mutableMapOf<DayOfWeek, WorkingState>()
        while (isAllDaysFilled(result).not()) {
            val workingState = getWorkingStateForCurrentDate(result, currentDay, extRestaurantData)

            workingState?.let { result[currentDay] = it }
            currentDay = currentDay.plus(1)
        }

        return RestaurantData(result)
    }

    /**
     * Check for business logic special cases and get appropriate [WorkingState] for current [DayOfWeek]
     */
    private fun getWorkingStateForCurrentDate(
            result: MutableMap<DayOfWeek, WorkingState>,
            currentDay: DayOfWeek,
            extRestaurantData: ExtRestaurantData
    ): WorkingState? {
        val openingHours = extRestaurantData.getOpeningHours(currentDay)
        val workingHoursBelongingToPreviousDay = getWorkingHoursBelongingToPreviousDay(openingHours, currentDay)
        return when {
            currentDay in result -> {
                null
            }

            openingHours.isEmpty() -> {
                WorkingState.Closed
            }

            workingHoursBelongingToPreviousDay != null -> {
                WorkingState.Working(workingHoursBelongingToPreviousDay.toSingletonList())
            }

            else -> {
                val workingHoursList = getWorkingHours(
                        openingHours,
                        currentDay,
                        result,
                        extRestaurantData
                )

                WorkingState.Working(workingHoursList)
            }
        }
    }

    /**
     * If there is the only [ExtOpeningHour] in current day operations and its type is [ExtType.CLOSE],
     * it means, that this operation closes working day, which started in one of previous days.
     */
    private fun getWorkingHoursBelongingToPreviousDay(
            openingHours: List<ExtOpeningHour>,
            currentDay: DayOfWeek
    ): WorkingHours? =
            if (openingHours.size == 1 && openingHours[0].type == ExtType.CLOSE) {
                getWorkingHours(
                        ExtWorkingOperationTime(
                                ExtOpeningHour(ExtType.OPEN, 0),
                                currentDay
                        ),
                        ExtWorkingOperationTime(
                                openingHours[0],
                                currentDay
                        )
                )
            } else {
                null
            }

    /**
     * Converts all [ExtOpeningHour] to [WorkingHours], starting with the first,
     * which [ExtOpeningHour.type] == [ExtType.OPEN] (first [ExtOpeningHour] with [ExtOpeningHour.type] == [ExtType.CLOSE], if exist, belongs to previous day).
     * If the last [ExtOpeningHour] has type [ExtType.OPEN], searches
     * the first [ExtOpeningHour] with [ExtOpeningHour.type] == [ExtType.CLOSE] in next days operations.
     */
    private fun getWorkingHours(
            openingHours: List<ExtOpeningHour>,
            currentDay: DayOfWeek,
            result: MutableMap<DayOfWeek, WorkingState>,
            extRestaurantData: ExtRestaurantData
    ): List<WorkingHours> {
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
                getClosingHour(currentDay, extRestaurantData, result)
            }

            val workingHours = getWorkingHours(open, close)
            workingHoursList.add(workingHours)
        }

        return workingHoursList
    }

    /**
     * If last operation type in current day is ExtType.OPEN, it means,
     * that restaurant closes at the next day. It is possible, that restaurant will
     * work more than 24h, so theoretically it can open on Monday at 00:00 and close on Sunday at 23:59:59.
     * Unfortunately, I didn't receive more precise limitations and conditions, so implementation
     * designed to handle described above corner case.
     * I assume, that in this case, there will be empty arrays of operations in the days, between
     * start day and end day. In this case, [WorkingState.OpenedWholeDay] will be settled for current [DayOfWeek]
     *
     * @throws ConvertException if input data is somehow incorrect
     */
    private fun getClosingHour(
            currentDay: DayOfWeek,
            extRestaurantData: ExtRestaurantData,
            result: MutableMap<DayOfWeek, WorkingState>
    ): ExtWorkingOperationTime {
        var closingDay = currentDay.plus(1)
        for (hours in extRestaurantData.toIteratorWrapper(closingDay)) {
            when {
                hours.isEmpty() -> {
                    result[closingDay] = WorkingState.OpenedWholeDay
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

    private fun isAllDaysFilled(map: Map<DayOfWeek, WorkingState>): Boolean = DayOfWeek
            .values()
            .all(map::containsKey)

    /**
     * In described below case, while starting to iterate from Monday, we don't know for sure,
     * what empty array in monday means - is it means, that Monday is closed,
     * or is it opened whole day. So that's why we need start to iterate from first day
     * with non-empty operations to have enough context.
     *
     *  "saturday": [
     *       {
     *           "type": "close",
     *           "value": 3600
     *       },
     *       {
     *           "type": "open",
     *           "value": 36000
     *       }
     *   ],
     *   "sunday": [],
     *   "monday": [],
     *   "tuesday": [
     *       {
     *           "type": "close",
     *           "value": 64800
     *       }
     *   ],
     *   ...
     *
     *  @return first [DayOfWeek], in which [List<ExtOpeningHour>] is not empty
     *  or null if in all [DayOfWeek] [List<ExtOpeningHour>] are empty
     */
    private fun getFirstDayWithNonEmptyWorkingHours(
            extRestaurantData: ExtRestaurantData
    ): DayOfWeek? = DayOfWeek
            .values()
            .firstOrNull {
                extRestaurantData.getOpeningHours(it).isNotEmpty()
            }

    /**
     * Checks input data for correctness.
     *
     * @throws ConvertException if input data is somehow incorrect
     */
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