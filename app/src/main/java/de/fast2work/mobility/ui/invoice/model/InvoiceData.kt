package de.fast2work.mobility.ui.invoice.model

data class InvoiceData(
    var invoiceName: String = "",
    var invoiceStatus: String = "",
    var invoiceDate: String = "",
    var invoiceType: String = "",
    var invoiceAmount: String = "",
    var invoiceColor: String = "",
) {
}