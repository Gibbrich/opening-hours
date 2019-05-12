package OpeningHours.domain.manager

import OpeningHours.domain.model.RestaurantData
import OpeningHours.getInputFile
import OpeningHours.parseData
import OpeningHours.utils.compareEquals
import org.junit.Assert.*
import kotlin.test.*
import org.junit.Test
import java.lang.IllegalArgumentException
import java.time.DayOfWeek

private const val BASIC_INPUT = "src/test/resources/basic_input.json"

class WorkingHoursConverterTest {

    @Test
    fun `basic input correct output`() {
        val input = BASIC_INPUT
        val output = "src/test/resources/basic_output.txt"
        compareEquals(input, output)
    }

    @Test
    fun `if a restaurant is closed the whole day, list of working hours is empty`() {
        val restaurantData = getRestaurantData(BASIC_INPUT)

        checkWorkingHoursAreEmpty(restaurantData, DayOfWeek.MONDAY)
        checkWorkingHoursAreEmpty(restaurantData, DayOfWeek.WEDNESDAY)
    }

    @Test
    fun `if a restaurant has only close operation, it belongs to previous day`() {
        val input = "src/test/resources/monday_contains_only_close_operation_input.json"
        val restaurantData = getRestaurantData(input)
        checkWorkingHoursAreEmpty(restaurantData, DayOfWeek.MONDAY)
    }

    @Test
    fun `exception thrown if a restaurant last operation of previous day and first operation of next day is open`() {
        val input = "src/test/resources/invalid_input/thursday_last_operation_open_friday_first_operation_open.json"
        val exception = assertFailsWith<IllegalArgumentException> {
            getRestaurantData(input)
        }

        assertEquals(WorkingHoursConverter.NO_CLOSE_HOUR, exception.message)
    }

    // todo - ask, whether restaurant can work > 24 hrs
    @Test
    fun `exception thrown if a restaurant last operation of previous day is open and next day working hours is empty`() {
        val input = "src/test/resources/invalid_input/thursday_last_operation_open_friday_empty.json"
        assertFailsWith<IllegalArgumentException> {
            getRestaurantData(input)
        }
    }

    @Test
    fun `exception thrown if a restaurant last operation of previous day and first operation of next day is close`() {
        val input = "src/test/resources/invalid_input/thursday_last_operation_close_friday_first_operation_close.json"
        assertFailsWith<IllegalArgumentException> {
            getRestaurantData(input)
        }
    }

    @Test
    fun `exception thrown if two the same type operations follows each other`() {
        val input = "src/test/resources/invalid_input/saturday_two_same_type_operations_follows_each_other.json"
        assertFailsWith<IllegalArgumentException> {
            getRestaurantData(input)
        }
    }

    @Test
    fun `exception thrown if working hour time less 0`() {
        val input = "src/test/resources/invalid_input/tuesday_open_time_less_0.json"
        assertFailsWith<IllegalArgumentException> {
            getRestaurantData(input)
        }
    }

    @Test
    fun `exception thrown if working hour time more 86399`() {
        val input = "src/test/resources/invalid_input/tuesday_close_time_more_86399.json"
        assertFailsWith<IllegalArgumentException> {
            getRestaurantData(input)
        }
    }

    private fun getRestaurantData(input: String): RestaurantData {
        val inputFile = getInputFile(input)
        val extRestaurantData = parseData(inputFile)
        return WorkingHoursConverter.getWorkingHours(extRestaurantData)
    }

    private fun checkWorkingHoursAreEmpty(
            restaurantData: RestaurantData,
            dayOfWeek: DayOfWeek
    ) {
        val workingHours = restaurantData.workingHours[dayOfWeek]
        assertNotNull(workingHours)
        // todo - rewrite to null
        assertTrue(workingHours!!.isEmpty())
    }
}