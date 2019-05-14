package OpeningHours.data

import java.time.DayOfWeek

data class ExtRestaurantDataWrapper(
        val restaurantData: ExtRestaurantData,
        val start: DayOfWeek
)

/**
 * Starts iteration from [ExtRestaurantDataWrapper.start] and goes through
 * all [DayOfWeek] up to [ExtRestaurantDataWrapper.start] exclusive.
 * i.e. starts from [DayOfWeek.MONDAY], ends at [DayOfWeek.SUNDAY], 6 iterations totally.
 */
operator fun ExtRestaurantDataWrapper.iterator(): Iterator<List<ExtOpeningHour>> = ExtRestaurantDataIterator(restaurantData, start)

private class ExtRestaurantDataIterator(
        private val restaurantData: ExtRestaurantData,
        private val start: DayOfWeek
) : Iterator<List<ExtOpeningHour>> {
    private var currentElement: DayOfWeek = start
    private var startedIteration = false

    override fun hasNext(): Boolean = startedIteration.not() || currentElement != start

    override fun next(): List<ExtOpeningHour> {
        if (hasNext()) {
            if (startedIteration.not()) {
                startedIteration = true
            }
            val openingHours = restaurantData.getOpeningHours(currentElement)
            currentElement = currentElement.plus(1)
            return openingHours
        } else {
            throw NoSuchElementException("You already iterated all elements")
        }
    }
}