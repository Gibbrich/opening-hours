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

fun DayOfWeek.values(): List<DayOfWeek> {
    val result = mutableListOf<DayOfWeek>()
    var temp = this
    val end = this

    do {
        result.add(temp)
        temp = temp.plus(1)
    } while (temp != end)

    return result
}

fun <T> T.toSingletonList() = listOf(this)