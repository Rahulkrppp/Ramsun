
package de.fast2work.mobility.utility.chart.notimportant;


import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.provider.ContactsContract;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import de.fast2work.mobility.R;
import de.fast2work.mobility.utility.chart.notimportant.charting.components.MarkerView;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.CandleEntry;
import de.fast2work.mobility.utility.chart.notimportant.charting.data.Entry;
import de.fast2work.mobility.utility.chart.notimportant.charting.highlight.Highlight;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.MPPointF;
import de.fast2work.mobility.utility.chart.notimportant.charting.utils.Utils;


/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
@SuppressLint("ViewConstructor")
public class MyMarkerView extends MarkerView {

    private final TextView tvContent;
    private boolean isStack1;

    public MyMarkerView(Context context, int layoutResource,boolean isStack ) {
        super(context, layoutResource);
        isStack1=isStack;

        tvContent = findViewById(R.id.tvContent);
    }

    @Override
    public void draw(Canvas canvas, float posX, float posY) {
        if (tvContent.getText()==""){
            return;
        }
        super.draw(canvas, posX, posY);

    }

    // runs every time the MarkerView is redrawn, can be used to update the
    // content (user-interface)
    @SuppressLint("SetTextI18n")
    @Override
    public void refreshContent(Entry e, Highlight highlight) {

        if (e instanceof CandleEntry) {

            CandleEntry ce = (CandleEntry) e;
            if (ce.getHigh()!=0.0){
                tvContent.setText(Float.toString(ce.getHigh()));
            }else {
                tvContent.setText("");
            }

        } else {
            if (!isStack1) {
                if (e.getData() != null) {
                    if ((Boolean) e.getData()) {
                        tvContent.setText(Float.toString(e.getY()));
                    } else {
                        tvContent.setText("");

                    }
                } else {
                    if (e.getY()!=0.0) {
                        tvContent.setText(Float.toString(e.getY()));
                    }else {
                        tvContent.setText("");
                    }
                }
            }
        }

        if (isStack1){
            if (((float[]) e.getData()).length>0) {
                float[] data = (float[]) e.getData();
                    Float value = (Float) data[(highlight.getStackIndex())];
                    //setLabel(String.valueOf(value));
                if (value!=0.0) {
                    tvContent.setText(Float.toString(value));
                }else {
                    tvContent.setText("");
                }
            } else {
                if (e.getY()!=0.0) {
                    tvContent.setText(Float.toString(e.getY()));
                }else {
                    tvContent.setText("");
                }

                //setLabel(String.valueOf(entry.getY()));
            }
        }

        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}

