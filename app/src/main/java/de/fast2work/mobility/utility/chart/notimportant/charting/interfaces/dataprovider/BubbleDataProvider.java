package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;

public interface BubbleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BubbleData getBubbleData();
}
