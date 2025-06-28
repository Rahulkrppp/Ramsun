package de.fast2work.mobility.data.response

data class PhotoIdLite(
    val properties: Properties = Properties(),
    val required: List<String> = listOf(),
    val title: String = "",
    val type: String = ""
)