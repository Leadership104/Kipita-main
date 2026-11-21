package com.mytum.model

import com.google.gson.annotations.SerializedName

data class DaysItemObject(

    @SerializedName("datetime") var datetime: String? = null,
//    @SerializedName("datetimeEpoch" ) var datetimeEpoch : Int?              = null,
    @SerializedName("tempmax") var tempmax: Double? = null,
    @SerializedName("tempmin") var tempmin: Double? = null,
    @SerializedName("conditions") var conditions: String? = null,
//    @SerializedName("temp"          ) var temp          : Double?           = null,
//    @SerializedName("feelslikemax"  ) var feelslikemax  : Int?              = null,
//    @SerializedName("feelslikemin"  ) var feelslikemin  : Int?              = null,
//    @SerializedName("feelslike"     ) var feelslike     : Double?           = null,
//    @SerializedName("humidity"      ) var humidity      : Double?           = null,
//    @SerializedName("precipprob"    ) var precipprob    : Int?              = null,
//    @SerializedName("preciptype"    ) var preciptype    : ArrayList<String> = arrayListOf(),
//    @SerializedName("snow"          ) var snow          : Int?              = null,
//    @SerializedName("snowdepth"     ) var snowdepth     : Int?              = null,
//    @SerializedName("windspeed"     ) var windspeed     : Double?           = null,
//    @SerializedName("winddir"       ) var winddir       : Double?           = null,
//    @SerializedName("pressure"      ) var pressure      : Double?           = null,
//    @SerializedName("cloudcover"    ) var cloudcover    : Double?           = null,
//    @SerializedName("visibility"    ) var visibility    : Double?           = null,
//    @SerializedName("severerisk"    ) var severerisk    : Int?              = null,
//    @SerializedName("windspeedmax"  ) var windspeedmax  : Double?           = null,
//    @SerializedName("windspeedmean" ) var windspeedmean : Double?           = null,
//    @SerializedName("windspeedmin"  ) var windspeedmin  : Double?           = null,
//    @SerializedName("sunrise"       ) var sunrise       : String?           = null,
//    @SerializedName("sunset"        ) var sunset        : String?           = null,
//    @SerializedName("conditions") var conditions    : String?           = null,
//    @SerializedName("description"   ) var description   : String?           = null,
    @SerializedName("icon") var icon: String? = null,
//    @SerializedName("aqius"         ) var aqius         : Int?              = null,
//    @SerializedName("aqieur"        ) var aqieur        : Int?              = null
) {
    var getDateTime
        get() = datetime ?: true
        set(value) {
            datetime = value.toString()
        }
    var getTempMex
        get() = tempmax 
        set(value) {
            tempmax = value
        }
    var getTempMin
        get() = tempmin
        set(value) {
            tempmin = value
        }
    var getCondition
        get() = conditions?: true
        set(value) {
            conditions = value.toString()
        }
    var getIcon
        get() = icon ?: true
        set(value) {
            icon = value.toString()
        }
}