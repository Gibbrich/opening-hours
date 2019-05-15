package OpeningHours.domain.manager

import OpeningHours.domain.model.RestaurantData
import OpeningHours.domain.model.WorkingHours
import OpeningHours.domain.model.WorkingOperationTime
import OpeningHours.domain.model.WorkingState
import OpeningHours.domain.utils.ConvertException
import OpeningHours.domain.utils.toSingletonList
import OpeningHours.getInputFile
import OpeningHours.parseData
import OpeningHours.utils.compareEquals
import org.junit.Assert.*
import org.junit.Test
import java.time.DayOfWeek
import java.util.*
import kotlin.test.assertFailsWith

class WorkingHoursConverterTest {

    @Test
    fun `if a restaurant is closed the whole day, list of working hours is empty`() {
        val restaurantData = getRestaurantData("src/test/resources/integration/random/basic_input.json")

        checkWorkingHoursAreEmpty(restaurantData, DayOfWeek.MONDAY)
        checkWorkingHoursAreEmpty(restaurantData, DayOfWeek.WEDNESDAY)
    }

    @Test
    fun `if a restaurant has only close operation, it belongs to previous day`() {
        val input = "src/test/resources/integration/random/monday_contains_only_close_operation_input.json"
        val restaurantData = getRestaurantData(input)

        val workingState = restaurantData.workingHours[DayOfWeek.MONDAY]
        assertNotNull(workingState)

        val expected = WorkingState.Working(
                WorkingHours(
                        WorkingOperationTime(Date(0), DayOfWeek.MONDAY),
                        WorkingOperationTime(Date(3600 * 1000), DayOfWeek.MONDAY)
                ).toSingletonList()
        )

        assertEquals(expected, workingState)
    }

    @Test
    fun `exception thrown if a restaurant last operation of previous day and first operation of next day is open`() {
        val input = "src/test/resources/invalid_input/thursday_last_operation_open_friday_first_operation_open.json"
        val exception = assertFailsWith<ConvertException> {
            getRestaurantData(input)
        }

        assertEquals(WorkingHoursConverter.NO_CLOSE_HOUR, exception.message)
    }

    @Test
    fun `exception thrown if a restaurant last operation of previous day and first operation of next day is close`() {
        val input = "src/test/resources/invalid_input/thursday_last_operation_close_friday_first_operation_close.json"
        assertFailsWith<ConvertException> {
            getRestaurantData(input)
        }
    }

    @Test
    fun `exception thrown if two the same type operations follows each other`() {
        val input = "src/test/resources/invalid_input/saturday_two_same_type_operations_follows_each_other.json"
        assertFailsWith<ConvertException> {
            getRestaurantData(input)
        }
    }

    @Test
    fun `exception thrown if working hour time less 0`() {
        val input = "src/test/resources/invalid_input/tuesday_open_time_less_0.json"
        assertFailsWith<ConvertException> {
            getRestaurantData(input)
        }
    }

    @Test
    fun `exception thrown if working hour time more 86399`() {
        val input = "src/test/resources/invalid_input/tuesday_close_time_more_86399.json"
        assertFailsWith<ConvertException> {
            getRestaurantData(input)
        }
    }

    private fun getRestaurantData(input: String): RestaurantData {
        val inputFile = getInputFile(input)
        val extRestaurantData = parseData(inputFile)
        return WorkingHoursConverter.getRestaurantData(extRestaurantData)
    }

    private fun checkWorkingHoursAreEmpty(
            restaurantData: RestaurantData,
            dayOfWeek: DayOfWeek
    ) {
        val workingHours = restaurantData.workingHours[dayOfWeek]
        assertNotNull(workingHours)
        assertTrue(workingHours is WorkingState.Closed)
    }
}