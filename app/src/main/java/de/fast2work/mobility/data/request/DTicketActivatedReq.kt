package de.fast2work.mobility.data.request

data class DTicketActivatedReq(
    var reactivation_key: String = "request.reActivationKey",
    /*var identification_medium: IdentificationMedium = IdentificationMedium(),
    var tariff_settings: TariffSettings = TariffSettings()*/
)