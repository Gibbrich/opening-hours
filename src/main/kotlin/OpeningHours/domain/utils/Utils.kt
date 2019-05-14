package OpeningHours.domain.utils

import java.time.DayOfWeek

fun DayOfWeek.range(end: DayOfWeek): List<DayOfWeek> {
    val result = mutableListOf<DayOfWeek>()
    var temp = this
    while (temp != end) {
        result.add(temp)
        temp = temp.plus(1)
    }
    return result
}

fun <T> T.toSingletonList() = listOf(this)

fun <T> getOrDie(item: T?, binding: String): T =
        item ?: throw ConvertException("'$binding' must not be null")