package de.fast2work.mobility.data.response

data class OneOf(
    val additionalProperties: Boolean = false,
    val properties: PropertiesX = PropertiesX(),
    val required: List<String> = listOf(),
    val type: String = ""
)