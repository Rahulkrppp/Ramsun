package de.fast2work.mobility.utility.chart.notimportant.charting.charts;

import android.content.Context;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;

import de.fast2work.mobility.utility.chart.notimportant.charting.charts.BarChart;
import de.fast2work.mobility.utility.chart.notimportant.charting.components.XAxis.XAxisPosition;
import de.fast2work.mobility.utility.chart.notimportant.charting.components.YAxis.AxisDependency;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarEntry;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.Highlight;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.HorizontalBarHighlighter;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IBarDataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.HorizontalBarChartRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.XAxisRendererHorizontalBarChart;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.YAxisRendererHorizontalBarChart;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.HorizontalViewPortHandler;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.MPPointF;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.TransformerHorizontalBarChart;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.Utils;

/**
 * BarChart with horizontal bar orientation. In this implementation, x- and y-axis are switched, meaning the YAxis class
 * represents the horizontal values and the XAxis class represents the vertical values.
 *
 * @author Philipp Jahoda
 */
public class HorizontalBarChart extends BarChart {

    public HorizontalBarChart(Context context) {
        super(context);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalBarChart(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected void init() {

        mViewPortHandler = new HorizontalViewPortHandler();

        super.init();

        mLeftAxisTransformer = new TransformerHorizontalBarChart(mViewPortHandler);
        mRightAxisTransformer = new TransformerHorizontalBarChart(mViewPortHandler);

        mRenderer = new HorizontalBarChartRenderer(this, mAnimator, mViewPortHandler);
        setHighlighter(new HorizontalBarHighlighter(this));

        mAxisRendererLeft = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisLeft, mLeftAxisTransformer);
        mAxisRendererRight = new YAxisRendererHorizontalBarChart(mViewPortHandler, mAxisRight, mRightAxisTransformer);
        mXAxisRenderer = new XAxisRendererHorizontalBarChart(mViewPortHandler, mXAxis, mLeftAxisTransformer, this);
    }

    private RectF mOffsetsBuffer = new RectF();

    protected void calculateLegendOffsets(RectF offsets) {

        offsets.left = 0.f;
        offsets.right = 0.f;
        offsets.top = 0.f;
        offsets.bottom = 0.f;

        if (mLegend == null || !mLegend.isEnabled() || mLegend.isDrawInsideEnabled())
            return;

        switch (mLegend.getOrientation()) {
            case VERTICAL:

                switch (mLegend.getHorizontalAlignment()) {
                    case LEFT:
                        offsets.left += Math.min(mLegend.mNeededWidth,
                                mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent())
                                + mLegend.getXOffset();
                        break;

                    case RIGHT:
                        offsets.right += Math.min(mLegend.mNeededWidth,
                                mViewPortHandler.getChartWidth() * mLegend.getMaxSizePercent())
                                + mLegend.getXOffset();
                        break;

                    case CENTER:

                        switch (mLegend.getVerticalAlignment()) {
                            case TOP:
                                offsets.top += Math.min(mLegend.mNeededHeight,
                                        mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                        + mLegend.getYOffset();
                                break;

                            case BOTTOM:
                                offsets.bottom += Math.min(mLegend.mNeededHeight,
                                        mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                        + mLegend.getYOffset();
                                break;

                            default:
                                break;
                        }
                }

                break;

            case HORIZONTAL:

                switch (mLegend.getVerticalAlignment()) {
                    case TOP:
                        offsets.top += Math.min(mLegend.mNeededHeight,
                                mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                + mLegend.getYOffset();

                        if (mAxisLeft.isEnabled() && mAxisLeft.isDrawLabelsEnabled())
                            offsets.top += mAxisLeft.getRequiredHeightSpace(
                                    mAxisRendererLeft.getPaintAxisLabels());
                        break;

                    case BOTTOM:
                        offsets.bottom += Math.min(mLegend.mNeededHeight,
                                mViewPortHandler.getChartHeight() * mLegend.getMaxSizePercent())
                                + mLegend.getYOffset();

                        if (mAxisRight.isEnabled() && mAxisRight.isDrawLabelsEnabled())
                            offsets.bottom += mAxisRight.getRequiredHeightSpace(
                                    mAxisRendererRight.getPaintAxisLabels());
                        break;

                    default:
                        break;
                }
                break;
        }
    }

    @Override
    public void calculateOffsets() {

        float offsetLeft = 0f, offsetRight = 0f, offsetTop = 0f, offsetBottom = 0f;

        calculateLegendOffsets(mOffsetsBuffer);

        offsetLeft += mOffsetsBuffer.left;
        offsetTop += mOffsetsBuffer.top;
        offsetRight += mOffsetsBuffer.right;
        offsetBottom += mOffsetsBuffer.bottom;

        // offsets for y-labels
        if (mAxisLeft.needsOffset()) {
            offsetTop += mAxisLeft.getRequiredHeightSpace(mAxisRendererLeft.getPaintAxisLabels());
        }

        if (mAxisRight.needsOffset()) {
            offsetBottom += mAxisRight.getRequiredHeightSpace(mAxisRendererRight.getPaintAxisLabels());
        }

        float xlabelwidth = mXAxis.mLabelRotatedWidth;

        if (mXAxis.isEnabled()) {

            // offsets for x-labels
            if (mXAxis.getPosition() == XAxisPosition.BOTTOM) {

                offsetLeft += xlabelwidth;

            } else if (mXAxis.getPosition() == XAxisPosition.TOP) {

                offsetRight += xlabelwidth;

            } else if (mXAxis.getPosition() == XAxisPosition.BOTH_SIDED) {

                offsetLeft += xlabelwidth;
                offsetRight += xlabelwidth;
            }
        }

        offsetTop += getExtraTopOffset();
        offsetRight += getExtraRightOffset();
        offsetBottom += getExtraBottomOffset();
        offsetLeft += getExtraLeftOffset();

        float minOffset = Utils.convertDpToPixel(mMinOffset);

        mViewPortHandler.restrainViewPort(
                Math.max(minOffset, offsetLeft),
                Math.max(minOffset, offsetTop),
                Math.max(minOffset, offsetRight),
                Math.max(minOffset, offsetBottom));

        if (mLogEnabled) {
            Log.i(LOG_TAG, "offsetLeft: " + offsetLeft + ", offsetTop: " + offsetTop + ", offsetRight: " +
                    offsetRight + ", offsetBottom: "
                    + offsetBottom);
            Log.i(LOG_TAG, "Content: " + mViewPortHandler.getContentRect().toString());
        }

        prepareOffsetMatrix();
        prepareValuePxMatrix();
    }

    @Override
    protected void prepareValuePxMatrix() {
        mRightAxisTransformer.prepareMatrixValuePx(mAxisRight.mAxisMinimum, mAxisRight.mAxisRange, mXAxis.mAxisRange,
                mXAxis.mAxisMinimum);
        mLeftAxisTransformer.prepareMatrixValuePx(mAxisLeft.mAxisMinimum, mAxisLeft.mAxisRange, mXAxis.mAxisRange,
                mXAxis.mAxisMinimum);
    }

    @Override
    protected float[] getMarkerPosition(Highlight high) {
        return new float[]{high.getDrawY(), high.getDrawX()};
    }

    @Override
    public void getBarBounds(BarEntry e, RectF outputRect) {

        RectF bounds = outputRect;
        IBarDataSet set = mData.getDataSetForEntry(e);

        if (set == null) {
            outputRect.set(Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE, Float.MIN_VALUE);
            return;
        }

        float y = e.getY();
        float x = e.getX();

        float barWidth = mData.getBarWidth();

        float top = x - barWidth / 2f;
        float bottom = x + barWidth / 2f;
        float left = y >= 0 ? y : 0;
        float right = y <= 0 ? y : 0;

        bounds.set(left, top, right, bottom);

        getTransformer(set.getAxisDependency()).rectValueToPixel(bounds);

    }

    protected float[] mGetPositionBuffer = new float[2];

    /**
     * Returns a recyclable MPPointF instance.
     *
     * @param e
     * @param axis
     * @return
     */
    @Override
    public MPPointF getPosition(Entry e, AxisDependency axis) {

        if (e == null)
            return null;

        float[] vals = mGetPositionBuffer;
        vals[0] = e.getY();
        vals[1] = e.getX();

        getTransformer(axis).pointValuesToPixel(vals);

        return MPPointF.getInstance(vals[0], vals[1]);
    }

    /**
     * Returns the Highlight object (contains x-index and DataSet index) of the selected value at the given touch point
     * inside the BarChart.
     *
     * @param x
     * @param y
     * @return
     */
    @Override
    public Highlight getHighlightByTouchPoint(float x, float y) {

        if (mData == null) {
            if (mLogEnabled)
                Log.e(LOG_TAG, "Can't select by touch. No data set.");
            return null;
        } else
            return getHighlighter().getHighlight(y, x); // switch x and y
    }

    @Override
    public float getLowestVisibleX() {
        getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                mViewPortHandler.contentBottom(), posForGetLowestVisibleX);
        float result = (float) Math.max(mXAxis.mAxisMinimum, posForGetLowestVisibleX.y);
        return result;
    }

    @Override
    public float getHighestVisibleX() {
        getTransformer(AxisDependency.LEFT).getValuesByTouchPoint(mViewPortHandler.contentLeft(),
                mViewPortHandler.contentTop(), posForGetHighestVisibleX);
        float result = (float) Math.min(mXAxis.mAxisMaximum, posForGetHighestVisibleX.y);
        return result;
    }

    /**
     * ###### VIEWPORT METHODS BELOW THIS ######
     */

    @Override
    public void setVisibleXRangeMaximum(float maxXRange) {
        float xScale = mXAxis.mAxisRange / (maxXRange);
        mViewPortHandler.setMinimumScaleY(xScale);
    }

    @Override
    public void setVisibleXRangeMinimum(float minXRange) {
        float xScale = mXAxis.mAxisRange / (minXRange);
        mViewPortHandler.setMaximumScaleY(xScale);
    }

    @Override
    public void setVisibleXRange(float minXRange, float maxXRange) {
        float minScale = mXAxis.mAxisRange / minXRange;
        float maxScale = mXAxis.mAxisRange / maxXRange;
        mViewPortHandler.setMinMaxScaleY(minScale, maxScale);
    }

    @Override
    public void setVisibleYRangeMaximum(float maxYRange, AxisDependency axis) {
        float yScale = getAxisRange(axis) / maxYRange;
        mViewPortHandler.setMinimumScaleX(yScale);
    }

    @Override
    public void setVisibleYRangeMinimum(float minYRange, AxisDependency axis) {
        float yScale = getAxisRange(axis) / minYRange;
        mViewPortHandler.setMaximumScaleX(yScale);
    }

    @Override
    public void setVisibleYRange(float minYRange, float maxYRange, AxisDependency axis) {
        float minScale = getAxisRange(axis) / minYRange;
        float maxScale = getAxisRange(axis) / maxYRange;
        mViewPortHandler.setMinMaxScaleX(minScale, maxScale);
    }
}
