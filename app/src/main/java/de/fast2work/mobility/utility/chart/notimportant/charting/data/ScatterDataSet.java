
package de.fast2work.mobility.utility.chart.notimportant.charting.data;

import de.fast2work.mobility.utility.chart.notimportant.charting.charts.ScatterChart;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.DataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.LineScatterCandleRadarDataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.interfaces.datasets.IScatterDataSet;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.ChevronDownShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.ChevronUpShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.CircleShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.CrossShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.IShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.SquareShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.TriangleShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.renderer.scatter.XShapeRenderer;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

public class ScatterDataSet extends LineScatterCandleRadarDataSet<de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry> implements IScatterDataSet {

    /**
     * the size the scattershape will have, in density pixels
     */
    private float mShapeSize = 15f;

    /**
     * Renderer responsible for rendering this DataSet, default: square
     */
    protected IShapeRenderer mShapeRenderer = new SquareShapeRenderer();

    /**
     * The radius of the hole in the shape (applies to Square, Circle and Triangle)
     * - default: 0.0
     */
    private float mScatterShapeHoleRadius = 0f;

    /**
     * Color for the hole in the shape.
     * Setting to `ColorTemplate.COLOR_NONE` will behave as transparent.
     * - default: ColorTemplate.COLOR_NONE
     */
    private int mScatterShapeHoleColor = ColorTemplate.COLOR_NONE;

    public ScatterDataSet(List<de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry> yVals, String label) {
        super(yVals, label);
    }

    @Override
    public DataSet<de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry> copy() {
        List<de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry> entries = new ArrayList<Entry>();
        for (int i = 0; i < mEntries.size(); i++) {
            entries.add(mEntries.get(i).copy());
        }
        ScatterDataSet copied = new ScatterDataSet(entries, getLabel());
        copy(copied);
        return copied;
    }

    protected void copy(ScatterDataSet scatterDataSet) {
        super.copy(scatterDataSet);
        scatterDataSet.mShapeSize = mShapeSize;
        scatterDataSet.mShapeRenderer = mShapeRenderer;
        scatterDataSet.mScatterShapeHoleRadius = mScatterShapeHoleRadius;
        scatterDataSet.mScatterShapeHoleColor = mScatterShapeHoleColor;
    }

    /**
     * Sets the size in density pixels the drawn scattershape will have. This
     * only applies for non custom shapes.
     *
     * @param size
     */
    public void setScatterShapeSize(float size) {
        mShapeSize = size;
    }

    @Override
    public float getScatterShapeSize() {
        return mShapeSize;
    }

    /**
     * Sets the ScatterShape this DataSet should be drawn with. This will search for an available IShapeRenderer and set this
     * renderer for the DataSet.
     *
     * @param shape
     */
    public void setScatterShape(ScatterChart.ScatterShape shape) {
        mShapeRenderer = getRendererForShape(shape);
    }

    /**
     * Sets a new IShapeRenderer responsible for drawing this DataSet.
     * This can also be used to set a custom IShapeRenderer aside from the default ones.
     *
     * @param shapeRenderer
     */
    public void setShapeRenderer(IShapeRenderer shapeRenderer) {
        mShapeRenderer = shapeRenderer;
    }

    @Override
    public IShapeRenderer getShapeRenderer() {
        return mShapeRenderer;
    }

    /**
     * Sets the radius of the hole in the shape (applies to Square, Circle and Triangle)
     * Set this to <= 0 to remove holes.
     *
     * @param holeRadius
     */
    public void setScatterShapeHoleRadius(float holeRadius) {
        mScatterShapeHoleRadius = holeRadius;
    }

    @Override
    public float getScatterShapeHoleRadius() {
        return mScatterShapeHoleRadius;
    }

    /**
     * Sets the color for the hole in the shape.
     *
     * @param holeColor
     */
    public void setScatterShapeHoleColor(int holeColor) {
        mScatterShapeHoleColor = holeColor;
    }

    @Override
    public int getScatterShapeHoleColor() {
        return mScatterShapeHoleColor;
    }

    public static IShapeRenderer getRendererForShape(ScatterChart.ScatterShape shape) {

        switch (shape) {
            case SQUARE:
                return new SquareShapeRenderer();
            case CIRCLE:
                return new CircleShapeRenderer();
            case TRIANGLE:
                return new TriangleShapeRenderer();
            case CROSS:
                return new CrossShapeRenderer();
            case X:
                return new XShapeRenderer();
            case CHEVRON_UP:
                return new ChevronUpShapeRenderer();
            case CHEVRON_DOWN:
                return new ChevronDownShapeRenderer();
        }

        return null;
    }
}
