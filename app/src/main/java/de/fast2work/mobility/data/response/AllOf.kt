package de.fast2work.mobility.data.response

data class AllOf(
    val additionalProperties: Boolean = false,
    val properties: PropertiesXX = PropertiesXX(),
    val required: List<String> = listOf(),
    val type: String = ""
)