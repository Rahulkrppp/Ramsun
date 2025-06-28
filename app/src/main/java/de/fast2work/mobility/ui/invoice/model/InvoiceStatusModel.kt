package de.fast2work.mobility.ui.invoice.model

data class InvoiceStatusModel(
    var statusTitle: String? = "",
    var title: String,
    var isSelected: Boolean = false
) {
}