
package de.fast2work.mobility.utility.chart.notimportant.charting.data;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.ChartData;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.Highlight;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IRadarDataSet;

import java.util.Arrays;
import java.util.List;

/**
 * Data container for the RadarChart.
 *
 * @author Philipp Jahoda
 */
public class RadarData extends ChartData<IRadarDataSet> {

    private List<String> mLabels;

    public RadarData() {
        super();
    }

    public RadarData(List<IRadarDataSet> dataSets) {
        super(dataSets);
    }

    public RadarData(IRadarDataSet... dataSets) {
        super(dataSets);
    }

    /**
     * Sets the labels that should be drawn around the RadarChart at the end of each web line.
     *
     * @param labels
     */
    public void setLabels(List<String> labels) {
        this.mLabels = labels;
    }

    /**
     * Sets the labels that should be drawn around the RadarChart at the end of each web line.
     *
     * @param labels
     */
    public void setLabels(String... labels) {
        this.mLabels = Arrays.asList(labels);
    }

    public List<String> getLabels() {
        return mLabels;
    }

    @Override
    public Entry getEntryForHighlight(Highlight highlight) {
        return getDataSetByIndex(highlight.getDataSetIndex()).getEntryForIndex((int) highlight.getX());
    }
}
