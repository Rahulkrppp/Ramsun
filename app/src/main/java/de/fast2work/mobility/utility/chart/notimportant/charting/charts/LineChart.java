
package de.fast2work.mobility.utility.chart.notimportant.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import de.fast2work.mobility.utility.chart.notimportant.charting.charts.BarLineChartBase;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.LineData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.LineDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.LineChartRenderer;

/**
 * Chart that draws lines, surfaces, circles, ...
 *
 * @author Philipp Jahoda
 */
public class LineChart extends BarLineChartBase<LineData> implements LineDataProvider {

    public LineChart(Context context) {
        super(context);
    }

    public LineChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public LineChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new LineChartRenderer(this, mAnimator, mViewPortHandler);
    }

    @Override
    public LineData getLineData() {
        return mData;
    }

    @Override
    protected void onDetachedFromWindow() {
        // releases the bitmap in the renderer to avoid oom error
        if (mRenderer != null && mRenderer instanceof LineChartRenderer) {
            ((LineChartRenderer) mRenderer).releaseBitmap();
        }
        super.onDetachedFromWindow();
    }
}
