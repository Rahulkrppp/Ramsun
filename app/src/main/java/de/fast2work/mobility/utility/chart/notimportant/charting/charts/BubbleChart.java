
package de.fast2work.mobility.utility.chart.notimportant.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import de.fast2work.mobility.utility.chart.notimportant.charting.charts.BarLineChartBase;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.BubbleDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.BubbleChartRenderer;

/**
 * The BubbleChart. Draws bubbles. Bubble chart implementation: Copyright 2015
 * Pierre-Marc Airoldi Licensed under Apache License 2.0. In the BubbleChart, it
 * is the area of the bubble, not the radius or diameter of the bubble that
 * conveys the data.
 *
 * @author Philipp Jahoda
 */
public class BubbleChart extends BarLineChartBase<BubbleData> implements BubbleDataProvider {

    public BubbleChart(Context context) {
        super(context);
    }

    public BubbleChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BubbleChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new BubbleChartRenderer(this, mAnimator, mViewPortHandler);
    }

    public BubbleData getBubbleData() {
        return mData;
    }
}
