package de.fast2work.mobility.utility.chart.notimportant.charting.data;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarLineScatterCandleBubbleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.ICandleDataSet;

import java.util.List;

public class CandleData extends BarLineScatterCandleBubbleData<ICandleDataSet> {

    public CandleData() {
        super();
    }

    public CandleData(List<ICandleDataSet> dataSets) {
        super(dataSets);
    }

    public CandleData(ICandleDataSet... dataSets) {
        super(dataSets);
    }
}
