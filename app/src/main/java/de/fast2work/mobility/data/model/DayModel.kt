package de.fast2work.mobility.data.model

import de.fast2work.mobility.data.response.ItemTransportDetails
import de.fast2work.mobility.data.response.ModeOfTransport

data class DayModel(var day:String,var modeOfTransport:ModeOfTransport?=null, var transportDetails: ItemTransportDetails?=null)