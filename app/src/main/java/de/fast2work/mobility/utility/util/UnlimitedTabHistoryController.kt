package de.fast2work.mobility.utility.util

import java.util.*

class UnlimitedTabHistoryController(fragNavPopController: FragNavPopController,
                                    fragNavSwitchController: FragNavSwitchController) : CollectionFragNavTabHistoryController(fragNavPopController, fragNavSwitchController) {
    private val tabHistory = Stack<Int>()

    override val collectionSize: Int
        get() = tabHistory.size

    override val andRemoveIndex: Int
        get() {
            tabHistory.pop()
            return tabHistory.pop()
        }

    override var history: ArrayList<Int>
        get() = ArrayList(tabHistory)
        set(history) {
            tabHistory.clear()
            tabHistory.addAll(history)
        }

    override fun switchTab(index: Int) {
        tabHistory.push(index)
    }
}
