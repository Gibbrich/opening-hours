package OpeningHours.utils

import OpeningHours.domain.manager.WorkingHoursConverter
import OpeningHours.domain.model.getDisplayString
import OpeningHours.getInputFile
import OpeningHours.parseData
import org.junit.Assert

fun compareEquals(input: String, output: String) {
    val inputFile = getInputFile(input)
    val data = parseData(inputFile)
    val restaurantData = WorkingHoursConverter.getRestaurantDataNew(data)
    val expected = getInputFile(output).readText()
    val result = restaurantData.getDisplayString()

    Assert.assertEquals(expected, result)
}