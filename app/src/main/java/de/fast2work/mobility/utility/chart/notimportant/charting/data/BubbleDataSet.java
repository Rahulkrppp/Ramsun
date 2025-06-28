
package de.fast2work.mobility.utility.chart.notimportant.charting.data;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.BarLineScatterCandleBubbleDataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleEntry;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.DataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IBubbleDataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.Utils;

import java.util.ArrayList;
import java.util.List;

public class BubbleDataSet extends BarLineScatterCandleBubbleDataSet<de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleEntry> implements IBubbleDataSet {

    protected float mMaxSize;
    protected boolean mNormalizeSize = true;

    private float mHighlightCircleWidth = 2.5f;

    public BubbleDataSet(List<de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleEntry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public void setHighlightCircleWidth(float width) {
        mHighlightCircleWidth = Utils.convertDpToPixel(width);
    }

    @Override
    public float getHighlightCircleWidth() {
        return mHighlightCircleWidth;
    }

    @Override
    protected void calcMinMax(de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleEntry e) {
        super.calcMinMax(e);

        final float size = e.getSize();

        if (size > mMaxSize) {
            mMaxSize = size;
        }
    }

    @Override
    public DataSet<de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleEntry> copy() {
        List<de.fast2work.mobility.utility.chart.notimportant.charting.data.BubbleEntry> entries = new ArrayList<BubbleEntry>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }
        BubbleDataSet copied = new BubbleDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(BubbleDataSet bubbleDataSet) {
        bubbleDataSet.mHighlightCircleWidth = mHighlightCircleWidth;
        bubbleDataSet.mNormalizeSize = mNormalizeSize;
    }

    @Override
    public float getMaxSize() {
        return mMaxSize;
    }

    @Override
    public boolean isNormalizeSizeEnabled() {
        return mNormalizeSize;
    }

    public void setNormalizeSizeEnabled(boolean normalizeSize) {
        mNormalizeSize = normalizeSize;
    }
}
