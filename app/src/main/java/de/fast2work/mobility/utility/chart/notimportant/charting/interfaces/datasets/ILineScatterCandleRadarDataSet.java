package de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets;

import android.graphics.DashPathEffect;

import de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IBarLineScatterCandleBubbleDataSet;

/**
 * Created by Philipp Jahoda on 21/10/15.
 */
public interface ILineScatterCandleRadarDataSet<T extends Entry> extends IBarLineScatterCandleBubbleDataSet<T> {

    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     * @return
     */
    boolean isVerticalHighlightIndicatorEnabled();

    /**
     * Returns true if vertical highlight indicator lines are enabled (drawn)
     * @return
     */
    boolean isHorizontalHighlightIndicatorEnabled();

    /**
     * Returns the line-width in which highlight lines are to be drawn.
     * @return
     */
    float getHighlightLineWidth();

    /**
     * Returns the DashPathEffect that is used for highlighting.
     * @return
     */
    DashPathEffect getDashPathEffectHighlight();
}
