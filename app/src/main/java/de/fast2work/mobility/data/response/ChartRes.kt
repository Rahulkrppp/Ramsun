package de.fast2work.mobility.data.response

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class ChartRes(
    @SerializedName("unique_vehicles") var uniqueVehicles: ArrayList<UniqueVehiclesRes>? = arrayListOf(),
    @SerializedName("emissions_by_vehicle_type") var emissionsByVehicleType: ArrayList<EmissionsByVehicleTypeRes>? = arrayListOf(),
    @SerializedName("kms_by_vehicle_type") var kmsByVehicleType: ArrayList<KmsVehicleTypeRes>? = arrayListOf(),
    @SerializedName("yearly_co2_trend") var yearlyCo2Trend: ArrayList<YearlyCo2TrendRes>? = arrayListOf(),
    @SerializedName("emissions_by_month_vehicle") var emissionsByMonthVehicle: ArrayList<Co2EmissionByMonthVehicleTypeRes>? = arrayListOf(),
    @SerializedName("last_year_comparison_by_month") var lastYearComparisonByMonth: ArrayList<LastTwoYearMonthRes>? = arrayListOf(),
    @SerializedName("last_year_comparison_by_vehicle") var lastYearComparisonByVehicle: ArrayList<LastTwoYearVehicleTypeRes>? = arrayListOf(),
) : Parcelable
