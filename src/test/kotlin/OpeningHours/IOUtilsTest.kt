package OpeningHours

import org.junit.Assert.*
import org.junit.Test
import java.io.File
import java.lang.Exception
import kotlin.test.assertFailsWith

private const val EMPTY_FILE = "src/test/resources/invalid_input/invalid_input_format.json"

class IOUtilsTest {

    @Test
    fun `getArguments with no arguments throws exception with no tests message`() {
        val exception = assertFailsWith<Exception> {
            getArguments(emptyArray())
        }

        assertEquals(NO_RESTAURANT_DATA, exception.message)
    }

    @Test
    fun `getArguments with more than 2 arguments throws exception with too many arguments message`() {
        val exception = assertFailsWith<Exception>(TOO_MANY_ARGUMENTS) {
            getArguments(arrayOf("first", "second", "third"))
        }

        assertEquals(TOO_MANY_ARGUMENTS, exception.message)
    }

    @Test
    fun `getArguments with 1 argument returns Arguments with #inputFilePath specified`() {
        val argument1 = "first"
        val result = getArguments(arrayOf(argument1))
        val expected = Arguments(argument1, null)
        assertEquals(expected, result)
    }

    @Test
    fun `getArguments with 2 argument returns Arguments with #inputFilePath and #outputFilePath specified`() {
        val argument1 = "first"
        val argument2 = "second"
        val result = getArguments(arrayOf(argument1, argument2))
        val expected = Arguments(argument1, argument2)
        assertEquals(expected, result)
    }

    @Test
    fun `parseData throws exception if paints data has incorrect format`() {
        val exception = assertFailsWith<Exception> {
            parseData(File(EMPTY_FILE))
        }

        assertEquals(INCORRECT_INPUT_DATA_FORMAT, exception.message)
    }
}