package OpeningHours.domain.manager

import org.junit.Test

private const val BASE_PATH = "src/test/resources/integration/basic"

class WorkingHoursConverterIntegrationTest {

    @Test
    fun basic() {
        compareEquals("basic_input.json", "basic_output.txt")
    }

    @Test
    fun `open-close several times per day`() {
        compareEquals("open_close_several_times_per_day_input.json", "open_close_several_times_per_day_output.txt")
    }

    @Test
    fun `start with close`() {
        compareEquals("start_with_close_input.json", "start_with_close_output.txt")
    }

    @Test
    fun `end with open`() {
        compareEquals("end_with_open_input.json", "end_with_open_output.txt")
    }

    @Test
    fun closed() {
        compareEquals("closed_input.json", "closed_output.txt")
    }

    @Test
    fun `opened whole day`() {
        compareEquals("opened_whole_day_input.json", "opened_whole_day_output.txt")
    }

    // todo - упомянуть в readme, что поведение как в input - штатное
    @Test
    fun `opened several days`() {
        compareEquals("opened_several_days_input.json", "opened_several_days_output.txt")
    }

    @Test
    fun empty() {
        compareEquals("empty_input.json", "empty_output.txt")
    }

    @Test
    fun `opened at the evening, closed at the morning in a week`() {
        compareEquals("opened_in_the_evening_closed_in_the_morning_in_a_week_input.json", "opened_in_the_evening_closed_in_the_morning_in_a_week_output.txt")
    }

    private fun compareEquals(input: String, output: String) {
        OpeningHours.utils.compareEquals("$BASE_PATH/$input", "$BASE_PATH/$output")
    }
}