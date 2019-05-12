package OpeningHours.data

import com.google.gson.annotations.SerializedName

data class ExtOpeningHour(
        @SerializedName("type")
        val type: ExtType,

        /**
         * Opening / closing time as UNIX time in seconds
         */
        @SerializedName("value")
        val value: Long
)