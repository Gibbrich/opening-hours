package OpeningHours.domain.manager

import org.junit.Test

private const val BASIC_PATH = "src/test/resources/integration/basic"
private const val RANDOM_PATH = "src/test/resources/integration/random"

class WorkingHoursConverterIntegrationTest {

    @Test
    fun common() {
        compareEquals("common_input.json", "common_output.txt", BASIC_PATH)
    }

    @Test
    fun `open-close several times per day`() {
        compareEquals("open_close_several_times_per_day_input.json", "open_close_several_times_per_day_output.txt", BASIC_PATH)
    }

    @Test
    fun `start with close`() {
        compareEquals("start_with_close_input.json", "start_with_close_output.txt", BASIC_PATH)
    }

    @Test
    fun `end with open`() {
        compareEquals("end_with_open_input.json", "end_with_open_output.txt", BASIC_PATH)
    }

    @Test
    fun closed() {
        compareEquals("closed_input.json", "closed_output.txt", BASIC_PATH)
    }

    @Test
    fun `opened whole day`() {
        compareEquals("opened_whole_day_input.json", "opened_whole_day_output.txt", BASIC_PATH)
    }

    @Test
    fun `opened several days`() {
        compareEquals("opened_several_days_input.json", "opened_several_days_output.txt", BASIC_PATH)
    }

    @Test
    fun empty() {
        compareEquals("empty_input.json", "empty_output.txt", BASIC_PATH)
    }

    @Test
    fun `opened at the evening, closed at the morning in a week`() {
        compareEquals("opened_in_the_evening_closed_in_the_morning_in_a_week_input.json", "opened_in_the_evening_closed_in_the_morning_in_a_week_output.txt", BASIC_PATH)
    }

    @Test
    fun `basic input correct output`() {
        compareEquals("basic_input.json", "basic_output.txt", RANDOM_PATH)
    }

    @Test
    fun `restaurant closes at the day after tomorrow`() {
        compareEquals("restaurant_open_on_saturday_close_on_monday_input.json", "restaurant_open_on_saturday_close_on_monday_output.txt", RANDOM_PATH)
    }

    @Test
    fun restaurant_open_on_thursday_at_10_AM_works_whole_week_and_close_on_next_thursday_at_1_AM() {
        compareEquals("restaurant_works_whole_week_input.json", "restaurant_works_whole_week_output.txt", RANDOM_PATH)
    }

    @Test
    fun restaurant_open_on_saturday_close_on_tuesday() {
        compareEquals("restaurant_open_on_saturday_close_on_tuesday_input.json", "restaurant_open_on_saturday_close_on_tuesday_output.txt", RANDOM_PATH)
    }

    @Test
    fun restaurant_open_on_monday_close_on_thursday() {
        compareEquals("restaurant_open_on_monday_close_on_tursday_input.json", "restaurant_open_on_monday_close_on_tursday_output.txt", RANDOM_PATH)
    }

    private fun compareEquals(input: String, output: String, path: String) {
        OpeningHours.utils.compareEquals("$path/$input", "$path/$output")
    }
}