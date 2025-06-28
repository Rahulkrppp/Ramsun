package de.fast2work.mobility.data.response

data class ProductInfo(
    val area_id: String = "",
    val currency: String = "",
    val customer_type: String = "",
    val duration_definition: String = "",
    val duration_in_minutes: Any = Any(),
    val earliest_activation_start_datetime: Any = Any(),
    val id: String = "",
    val identification_medium_schema: IdentificationMediumSchema = IdentificationMediumSchema(),
    val is_subscription: Boolean = false,
    val local_ticket_name: String = "",
    val local_validity_description: String = "",
    val possible_rendering_options: List<String> = listOf(),
    val price_in_cents: Int = 0,
    val recommended_successor_is: String = "",
    val recommended_successor_of: String = "",
    val sold_from: Any = Any(),
    val sold_until: String = "",
    val tariff_settings_schema: TariffSettingsSchema = TariffSettingsSchema(),
    val ticket_type: String = "",
    val validity_in_minutes: Any = Any()
)