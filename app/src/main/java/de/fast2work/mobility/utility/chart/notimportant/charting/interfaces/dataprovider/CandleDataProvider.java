package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.CandleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;

public interface CandleDataProvider extends BarLineScatterCandleBubbleDataProvider {

    CandleData getCandleData();
}
