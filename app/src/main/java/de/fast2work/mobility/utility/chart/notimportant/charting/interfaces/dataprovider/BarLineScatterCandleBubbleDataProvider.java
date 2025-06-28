package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider;

import de.fast2work.mobility.utility.chart.notimportant.charting.components.YAxis.AxisDependency;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarLineScatterCandleBubbleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.ChartInterface;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.Transformer;

public interface BarLineScatterCandleBubbleDataProvider extends ChartInterface {

    Transformer getTransformer(AxisDependency axis);
    boolean isInverted(AxisDependency axis);
    
    float getLowestVisibleX();
    float getHighestVisibleX();

    BarLineScatterCandleBubbleData getData();
}
