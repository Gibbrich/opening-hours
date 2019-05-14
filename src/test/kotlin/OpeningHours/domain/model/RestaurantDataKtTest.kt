package OpeningHours.domain.model

import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.util.*

class RestaurantDataKtTest {

    @Test
    fun `getDisplayString separates working hours using comma, if working hours for a day is empty, prints Closed`() {
        val restaurantData = RestaurantData(mapOf(
                DayOfWeek.MONDAY to WorkingState.Working(listOf(
                        WorkingHours(
                                WorkingOperationTime(Date(36000 * 1000L), DayOfWeek.MONDAY),
                                WorkingOperationTime(Date(64800 * 1000L), DayOfWeek.MONDAY)
                        ),
                        WorkingHours(
                                WorkingOperationTime(Date(68400 * 1000L), DayOfWeek.MONDAY),
                                WorkingOperationTime(Date(72000 * 1000L), DayOfWeek.MONDAY)
                        )
                ))
        ))

        val result = restaurantData.getDisplayString()

        val expected = """
            Monday: 10 AM - 6 PM, 7 PM - 8 PM
            Tuesday: Closed
            Wednesday: Closed
            Thursday: Closed
            Friday: Closed
            Saturday: Closed
            Sunday: Closed
        """.trimIndent()

        assertEquals(expected, result)
    }
}