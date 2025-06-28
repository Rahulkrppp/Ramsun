package de.fast2work.mobility.utility.util

interface FragNavSwitchController {
    fun switchTab(@FragNavController.TabIndex index: Int, transactionOptions: FragNavTransactionOptions?)
}
