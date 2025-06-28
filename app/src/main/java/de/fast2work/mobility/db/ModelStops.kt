package de.fast2work.mobility.db

import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.ModeOfTransport


//@Entity(tableName = "tbl_stops")
data class ModelStops(
    var transportDay: String="",
    var workingMode: String="",
    var transportMode: String="",
    var transportStartAddress: String="",
    var transportStopAddress: String="",
    var transportStartAddressLatitude: String="",
    var transportStartAddressLongitude: String="",
    var transportStopAddressLatitude: String="",
    var transportStopAddressLongitude: String="",
    var transportMeans: ModeOfTransport?=null,
    var transportDetails: ItemTransportDetails?=null,
    var isShared: Boolean=true,
    var numberOfPeople: String="",
    var isShareVisible:Boolean = false,
    var maximumNoOfPeople:Int = 0,
    var isPrefillData:Boolean = false
    )
