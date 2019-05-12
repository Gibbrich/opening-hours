package OpeningHours.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.util.*

class WorkingHoursKtTest {

    @Test
    fun `getDisplayString format open and close and join with dash`() {
        val workingHours = WorkingHours(
                Date(36000 * 1000L),
                Date(64800 * 1000L)
        )

        val expected = "10 AM - 6 PM"
        val result = workingHours.getDisplayString()

        assertEquals(expected, result)
    }
}