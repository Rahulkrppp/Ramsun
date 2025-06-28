
package de.fast2work.mobility.utility.chart.notimportant.charting.charts;

import android.content.Context;
import android.util.AttributeSet;

import de.fast2work.mobility.utility.chart.notimportant.charting.charts.BarLineChartBase;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.CandleData;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.dataprovider.CandleDataProvider;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.CandleStickChartRenderer;

/**
 * Financial chart type that draws candle-sticks (OHCL chart).
 *
 * @author Philipp Jahoda
 */
public class CandleStickChart extends BarLineChartBase<CandleData> implements CandleDataProvider {

    public CandleStickChart(Context context) {
        super(context);
    }

    public CandleStickChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CandleStickChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {
        super.init();

        mRenderer = new CandleStickChartRenderer(this, mAnimator, mViewPortHandler);

        getXAxis().setSpaceMin(0.5f);
        getXAxis().setSpaceMax(0.5f);
    }

    @Override
    public CandleData getCandleData() {
        return mData;
    }
}
