package de.fast2work.mobility.data.request

data class DTicketCreatePaymentIntent(
    var activation_start_datetime: String = "",
    var birth_date: String = "",
    var first_name: String = "",
    var german_postal_code: String = "",
    var last_name: String = "",
    var product_id: Int = 0
)