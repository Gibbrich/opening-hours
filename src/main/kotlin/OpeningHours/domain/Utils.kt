package OpeningHours.domain

import java.time.DayOfWeek

infix fun DayOfWeek.minus(dayOfWeek: DayOfWeek): Int {
    var temp = this
    var result = 0
    while (temp != dayOfWeek) {
        temp = temp.minus(1)
        result++
    }
    return result
}

fun DayOfWeek.range(end: DayOfWeek): List<DayOfWeek> {
    val result = mutableListOf<DayOfWeek>()
    var temp = this
    while (temp != end) {
        result.add(temp)
        temp = temp.plus(1)
    }
    return result
}