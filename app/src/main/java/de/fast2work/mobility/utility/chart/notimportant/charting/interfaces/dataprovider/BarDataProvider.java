package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;

public interface BarDataProvider extends BarLineScatterCandleBubbleDataProvider {

    BarData getBarData();
    boolean isDrawBarShadowEnabled();
    boolean isDrawValueAboveBarEnabled();
    boolean isHighlightFullBarEnabled();
}
