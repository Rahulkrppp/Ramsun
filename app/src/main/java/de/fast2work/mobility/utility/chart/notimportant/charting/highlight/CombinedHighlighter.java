package de.fast2work.mobility.utility.chart.notimportant.charting.highlight;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarData;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarLineScatterCandleBubbleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.ChartData;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.DataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.BarHighlighter;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.ChartHighlighter;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.Highlight;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.IHighlighter;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BarDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.CombinedDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IDataSet;

import java.util.List;

/**
 * Created by Philipp Jahoda on 12/09/15.
 */
public class CombinedHighlighter extends ChartHighlighter<CombinedDataProvider> implements IHighlighter
{

    /**
     * bar highlighter for supporting stacked highlighting
     */
    protected de.fast2work.mobility.utility.chart.notimportant.charting.highlight.BarHighlighter barHighlighter;

    public CombinedHighlighter(CombinedDataProvider chart, BarDataProvider barChart) {
        super(chart);

        // if there is BarData, create a BarHighlighter
        barHighlighter = barChart.getBarData() == null ? null : new BarHighlighter(barChart);
    }

    @Override
    protected List<Highlight> getHighlightsAtXValue(float xVal, float x, float y) {

        mHighlightBuffer.clear();

        List<BarLineScatterCandleBubbleData> dataObjects = mChart.getCombinedData().getAllData();

        for (int i = 0; i < dataObjects.size(); i++) {

            ChartData dataObject = dataObjects.get(i);

            // in case of BarData, let the BarHighlighter take over
            if (barHighlighter != null && dataObject instanceof BarData) {
                Highlight high = barHighlighter.getHighlight(x, y);

                if (high != null) {
                    high.setDataIndex(i);
                    mHighlightBuffer.add(high);
                }
            } else {

                for (int j = 0, dataSetCount = dataObject.getDataSetCount(); j < dataSetCount; j++) {

                    IDataSet dataSet = dataObjects.get(i).getDataSetByIndex(j);

                    // don't include datasets that cannot be highlighted
                    if (!dataSet.isHighlightEnabled())
                        continue;

                    List<Highlight> highs = buildHighlights(dataSet, j, xVal, DataSet.Rounding.CLOSEST);
                    for (Highlight high : highs)
                    {
                        high.setDataIndex(i);
                        mHighlightBuffer.add(high);
                    }
                }
            }
        }

        return mHighlightBuffer;
    }

//    protected Highlight getClosest(float x, float y, Highlight... highs) {
//
//        Highlight closest = null;
//        float minDistance = Float.MAX_VALUE;
//
//        for (Highlight high : highs) {
//
//            if (high == null)
//                continue;
//
//            float tempDistance = getDistance(x, y, high.getXPx(), high.getYPx());
//
//            if (tempDistance < minDistance) {
//                minDistance = tempDistance;
//                closest = high;
//            }
//        }
//
//        return closest;
//    }
}
