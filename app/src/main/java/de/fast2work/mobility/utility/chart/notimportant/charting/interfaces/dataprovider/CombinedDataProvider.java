package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.CombinedData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BarDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BubbleDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.CandleDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.LineDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.ScatterDataProvider;

/**
 * Created by philipp on 11/06/16.
 */
public interface CombinedDataProvider extends LineDataProvider, BarDataProvider, BubbleDataProvider, CandleDataProvider, ScatterDataProvider {

    CombinedData getCombinedData();
}
