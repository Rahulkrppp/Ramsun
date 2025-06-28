package de.fast2work.mobility.data.response

data class DTicketRes(
    val priceInCents: Int = 0,
    val productCode: String = "",
    val productId: Int = 0,
    val productInfo: ProductInfo = ProductInfo(),
    val productName: String = ""
)