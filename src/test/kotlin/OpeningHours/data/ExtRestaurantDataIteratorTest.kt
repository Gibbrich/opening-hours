package OpeningHours.data

import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import kotlin.test.assertFailsWith

class ExtRestaurantDataIteratorTest {

    @Test
    fun `iterator iterates at most 7 times`() {
        val monday = emptyList<ExtOpeningHour>()
        val tuesday = emptyList<ExtOpeningHour>()
        val wednesday = emptyList<ExtOpeningHour>()
        val thursday = emptyList<ExtOpeningHour>()
        val friday = emptyList<ExtOpeningHour>()
        val saturday = emptyList<ExtOpeningHour>()
        val sunday = emptyList<ExtOpeningHour>()

        val extRestaurantData = ExtRestaurantData(
                monday = monday,
                tuesday = tuesday,
                wednesday = wednesday,
                thursday = thursday,
                friday = friday,
                saturday = saturday,
                sunday = sunday
        )

        val iterator = extRestaurantData.toIteratorWrapper(DayOfWeek.WEDNESDAY).iterator()

        assertEquals(wednesday, iterator.next())
        assertEquals(thursday, iterator.next())
        assertEquals(friday, iterator.next())
        assertEquals(saturday, iterator.next())
        assertEquals(sunday, iterator.next())
        assertEquals(monday, iterator.next())
        assertEquals(tuesday, iterator.next())

        assertFailsWith<NoSuchElementException> {
            iterator.next()
        }
    }
}