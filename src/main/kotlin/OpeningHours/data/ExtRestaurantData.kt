package OpeningHours.data

import com.google.gson.annotations.SerializedName

// todo ask, whether data can be incorrect; whether data can be unsorted in time order; whether sunday may close in monday
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