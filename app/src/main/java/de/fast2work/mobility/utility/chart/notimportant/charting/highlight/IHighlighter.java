package de.fast2work.mobility.utility.chart.notimportant.charting.highlight;

import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.Highlight;

/**
 * Created by philipp on 10/06/16.
 */
public interface IHighlighter
{

    /**
     * Returns a Highlight object corresponding to the given x- and y- touch positions in pixels.
     *
     * @param x
     * @param y
     * @return
     */
    Highlight getHighlight(float x, float y);
}
