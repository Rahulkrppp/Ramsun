package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import de.fast2work.mobility.data.request.DayDataReq
import kotlinx.parcelize.Parcelize

@Parcelize
data class Co2SurveyGetDataResp(@SerializedName("surveyId")
                           var surveyId : Int = 0,

                           @SerializedName("sourceAddress")
                           var fromAddress : String = "",

                           @SerializedName("sourcelatitude")
                           var fromLatitude : String = "",

                           @SerializedName("sourcelongitude")
                           var fromLongitude : String ="",
                           @SerializedName("destinationAddress")
                           var toAddress : String = "",
                           @SerializedName("destinationlatitude")
                           var toLatitude : String = "",

                           @SerializedName("destinationlongitude")
                           var toLongitude : String = "",
                           @SerializedName("dayWiseCommuteInfo")
                           var dayData : ArrayList<DayInformation> = arrayListOf(),) : Parcelable

@Parcelize
data class DayInformation(@SerializedName("day")
                                var day : Int = 0,
                                @SerializedName("workFrom")
                                var workFrom : String = "",
                                @SerializedName("transportType")
                                var transportType : String? = null,
                                @SerializedName("journeyInfo")
                                var journeyData : ArrayList<JourneyInformation> = arrayListOf(),
                              ) : Parcelable

@Parcelize
data class JourneyInformation(
                            @SerializedName("transportMode")
                            var transportMode : String = "",

                            @SerializedName("transportChildMode")
                            var transportChildMode : String = "",

                            @SerializedName("sourceAddress")
                            var sourceAddress : String? = null,

                            @SerializedName("sourcelatitude")
                            var sourcelatitude : String? = null,

                            @SerializedName("sourcelongitude")
                            var sourcelongitude : String? = null,

                            @SerializedName("destinationAddress")
                            var destinationAddress : String ? = null,

                            @SerializedName("destinationlatitude")
                            var destinationlatitude : String ? = null,

                            @SerializedName("destinationlongitude")
                            var destinationlongitude : String ? = null,

                            @SerializedName("isShared")
                            var isShared : Boolean = false,

                            @SerializedName("noOfPeople")
                            var noOfPeople : Int = 0,


) : Parcelable
