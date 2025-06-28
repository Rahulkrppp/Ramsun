package de.fast2work.mobility.utility.util

import android.os.Bundle


class CurrentTabHistoryController(fragNavPopController: FragNavPopController) : BaseFragNavTabHistoryController(fragNavPopController) {

    @Throws(UnsupportedOperationException::class)
    override fun popFragments(popDepth: Int,
                              transactionOptions: FragNavTransactionOptions?): Boolean {
        return fragNavPopController.tryPopFragments(popDepth, transactionOptions) > 0
    }

    override fun switchTab(index: Int) {}

    override fun onSaveInstanceState(outState: Bundle) {}

    override fun restoreFromBundle(savedInstanceState: Bundle?) {}
}
