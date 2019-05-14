package OpeningHours.data

import OpeningHours.domain.utils.getOrDie
import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek

data class ExtRestaurantData(
        @SerializedName("monday")
        val monday: List<ExtOpeningHour>? = null,

        @SerializedName("tuesday")
        val tuesday: List<ExtOpeningHour>? = null,

        @SerializedName("wednesday")
        val wednesday: List<ExtOpeningHour>? = null,

        @SerializedName("thursday")
        val thursday: List<ExtOpeningHour>? = null,

        @SerializedName("friday")
        val friday: List<ExtOpeningHour>? = null,

        @SerializedName("saturday")
        val saturday: List<ExtOpeningHour>? = null,

        @SerializedName("sunday")
        val sunday: List<ExtOpeningHour>? = null

)

fun ExtRestaurantData.toIteratorWrapper(start: DayOfWeek) = ExtRestaurantDataWrapper(this, start)

fun ExtRestaurantData.getOpeningHours(dayOfWeek: DayOfWeek) = when (dayOfWeek) {
    DayOfWeek.MONDAY -> getOrDie(monday, "monday")
    DayOfWeek.TUESDAY -> getOrDie(tuesday, "tuesday")
    DayOfWeek.WEDNESDAY -> getOrDie(wednesday, "wednesday")
    DayOfWeek.THURSDAY -> getOrDie(thursday, "thursday")
    DayOfWeek.FRIDAY -> getOrDie(friday, "friday")
    DayOfWeek.SATURDAY -> getOrDie(saturday, "saturday")
    DayOfWeek.SUNDAY -> getOrDie(sunday, "sunday")
}