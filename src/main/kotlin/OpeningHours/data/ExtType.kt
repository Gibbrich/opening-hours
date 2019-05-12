package OpeningHours.data

import com.google.gson.annotations.SerializedName

enum class ExtType {
    @SerializedName("open")
    OPEN,

    @SerializedName("close")
    CLOSE
}