package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.ScatterData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;

public interface ScatterDataProvider extends BarLineScatterCandleBubbleDataProvider {

    ScatterData getScatterData();
}
