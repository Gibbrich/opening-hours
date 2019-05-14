package OpeningHours.data

import com.google.gson.annotations.SerializedName
import java.time.DayOfWeek

data class ExtRestaurantData(
        @SerializedName("monday")
        val monday: List<ExtOpeningHour>,

        @SerializedName("tuesday")
        val tuesday: List<ExtOpeningHour>,

        @SerializedName("wednesday")
        val wednesday: List<ExtOpeningHour>,

        @SerializedName("thursday")
        val thursday: List<ExtOpeningHour>,

        @SerializedName("friday")
        val friday: List<ExtOpeningHour>,

        @SerializedName("saturday")
        val saturday: List<ExtOpeningHour>,

        @SerializedName("sunday")
        val sunday: List<ExtOpeningHour>

)

fun ExtRestaurantData.toIteratorWrapper(start: DayOfWeek) = ExtRestaurantDataWrapper(this, start)

fun ExtRestaurantData.getOpeningHours(dayOfWeek: DayOfWeek) = when (dayOfWeek) {
    DayOfWeek.MONDAY -> monday
    DayOfWeek.TUESDAY -> tuesday
    DayOfWeek.WEDNESDAY -> wednesday
    DayOfWeek.THURSDAY -> thursday
    DayOfWeek.FRIDAY -> friday
    DayOfWeek.SATURDAY -> saturday
    DayOfWeek.SUNDAY -> sunday
}