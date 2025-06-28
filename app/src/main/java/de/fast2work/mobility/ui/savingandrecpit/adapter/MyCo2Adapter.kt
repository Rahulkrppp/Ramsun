package de.fast2work.mobility.ui.savingandrecpit.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import de.fast2work.mobility.ui.savingandrecpit.co2receipt.Co2ReceiptFragment
import de.fast2work.mobility.ui.savingandrecpit.co2saving.MyCo2SavingsFragment

class MyCo2Adapter (fragmentManager: FragmentManager, val lifecycle: Lifecycle) : FragmentStateAdapter(fragmentManager, lifecycle) {
    override fun getItemCount(): Int {
        return 2
    }

    override fun createFragment(position: Int): Fragment {
        if (position == 0) {
            return MyCo2SavingsFragment()
        }
        if (position == 1) {
            return Co2ReceiptFragment()
        }
        return MyCo2SavingsFragment()
    }
}