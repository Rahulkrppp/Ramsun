
package de.fast2work.mobility.utility.chart.notimportant.charting.data;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarLineScatterCandleBubbleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.ILineDataSet;

import java.util.List;

/**
 * Data object that encapsulates all data associated with a LineChart.
 * 
 * @author Philipp Jahoda
 */
public class LineData extends BarLineScatterCandleBubbleData<ILineDataSet> {

    public LineData() {
        super();
    }

    public LineData(ILineDataSet... dataSets) {
        super(dataSets);
    }

    public LineData(List<ILineDataSet> dataSets) {
        super(dataSets);
    }
}
