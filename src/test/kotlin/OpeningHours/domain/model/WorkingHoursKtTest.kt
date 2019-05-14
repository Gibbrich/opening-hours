package OpeningHours.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.util.*

class WorkingHoursKtTest {

    @Test
    fun `getDisplayString format open and close and join with dash`() {
        val workingHours = WorkingHours(
                WorkingOperationTime(Date(36000 * 1000L), DayOfWeek.MONDAY),
                WorkingOperationTime(Date(64800 * 1000L), DayOfWeek.MONDAY)
        )

        val expected = "10 AM - 6 PM"
        val result = workingHours.getDisplayString()

        assertEquals(expected, result)
    }
}