package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarEntry;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.Fill;

import java.util.List;

/**
 * Created by philipp on 21/10/15.
 */
public interface IBarDataSet extends IBarLineScatterCandleBubbleDataSet<BarEntry> {

    List<Fill> getFills();

    Fill getFill(int index);

    /**
     * Returns true if this DataSet is stacked (stacksize > 1) or not.
     *
     * @return
     */
    boolean isStacked();

    /**
     * Returns the maximum number of bars that can be stacked upon another in
     * this DataSet. This should return 1 for non stacked bars, and > 1 for stacked bars.
     *
     * @return
     */
    int getStackSize();

    /**
     * Returns the color used for drawing the bar-shadows. The bar shadows is a
     * surface behind the bar that indicates the maximum value.
     *
     * @return
     */
    int getBarShadowColor();

    /**
     * Returns the width used for drawing borders around the bars.
     * If borderWidth == 0, no border will be drawn.
     *
     * @return
     */
    float getBarBorderWidth();

    /**
     * Returns the color drawing borders around the bars.
     *
     * @return
     */
    int getBarBorderColor();

    /**
     * Returns the alpha value (transparency) that is used for drawing the
     * highlight indicator.
     *
     * @return
     */
    int getHighLightAlpha();


    /**
     * Returns the labels used for the different value-stacks in the legend.
     * This is only relevant for stacked bar entries.
     *
     * @return
     */
    String[] getStackLabels();
}
