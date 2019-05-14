package OpeningHours.domain.manager

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class WorkingHoursDateFormatterTest {
    @Test
    fun `format is "hh a" if minutes and seconds are zeros`() {
        val millis = 60 * 60 * 2 * 1000L
        val result = WorkingHoursDateFormatter.format(Date(millis))
        assertEquals("2 AM", result)
    }

    @Test
    fun `format is "hh mm a" if seconds are zeros`() {
        val millis = 56 * 60 * 2 * 1000L
        val result = WorkingHoursDateFormatter.format(Date(millis))
        assertEquals("1.52 AM", result)
    }

    @Test
    fun `format is "hh mm ss a" if all values are not zeroes`() {
        val millis = (60 * 60 + 215) * 1000L
        val result = WorkingHoursDateFormatter.format(Date(millis))
        assertEquals("1.03:35 AM", result)
    }

    @Test
    fun `formatted MAX_CLOSE_TIME equals 11 59 59 PM`() {
        val millis = WorkingHoursConverter.MAX_CLOSE_TIME * 1000L
        val result = WorkingHoursDateFormatter.format(Date(millis))
        assertEquals("11.59:59 PM", result)
    }

    @Test
    fun `formatted MIN_CLOSE_TIME equals 0 AM`() {
        val millis = WorkingHoursConverter.MIN_OPEN_TIME * 1000L
        val result = WorkingHoursDateFormatter.format(Date(millis))
        assertEquals("12 AM", result)
    }
}