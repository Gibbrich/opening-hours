package OpeningHours.domain.utils

import java.time.DayOfWeek

fun <T> T.toSingletonList() = listOf(this)

fun <T> getOrDie(item: T?, binding: String): T =
        item ?: throw ConvertException("'$binding' must not be null")