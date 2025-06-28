package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider;

import de.fast2work.mobility.utility.chart.notimportant.charting.components.YAxis;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.LineData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BarLineScatterCandleBubbleDataProvider;

public interface LineDataProvider extends BarLineScatterCandleBubbleDataProvider {

    LineData getLineData();

    YAxis getAxis(YAxis.AxisDependency dependency);
}
