package OpeningHours.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.util.*

class WorkingHoursKtTest {

    @Test
    fun `getDisplayString format open and close and join with dash if operation day of week is the same and close time more than open time`() {
        check(
                Date(36000 * 1000L),
                Date(64800 * 1000L),
                DayOfWeek.MONDAY,
                DayOfWeek.MONDAY,
                "10 AM - 6 PM"
        )
    }

    @Test
    fun `getDisplayString format open and close and join with dash if close operation day of week more at 1 day than open`() {
        check(
                Date(36000 * 1000L),
                Date(64800 * 1000L),
                DayOfWeek.MONDAY,
                DayOfWeek.TUESDAY,
                "10 AM - 6 PM"
        )
    }

    @Test
    fun `getDisplayString adds week day name at the end if close operation day more then 2 days than open`() {
        check(
                Date(36000 * 1000L),
                Date(64800 * 1000L),
                DayOfWeek.MONDAY,
                DayOfWeek.WEDNESDAY,
                "10 AM - 6 PM at Wednesday"
        )
    }

    @Test
    fun `getDisplayString adds next week day name if operation day of week is the same and close time less than open time`() {
        check(
                Date(64800 * 1000L),
                Date(36000 * 1000L),
                DayOfWeek.MONDAY,
                DayOfWeek.MONDAY,
                "6 PM - 10 AM at next Monday"
        )
    }

    private fun check(
            open: Date,
            close: Date,
            openWeekDay: DayOfWeek,
            closeWeekDay: DayOfWeek,
            expected: String
    ) {
        val workingHours = WorkingHours(
                WorkingOperationTime(open, openWeekDay),
                WorkingOperationTime(close, closeWeekDay)
        )

        val result = workingHours.getDisplayString()

        assertEquals(expected, result)
    }
}