package com.nidaappdev.brainer.customViews.StatsClasses;

import android.content.Context;
import android.widget.TextView;

import com.nidaappdev.brainer.R;
import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;

import java.text.DecimalFormat;

public class RadarMarkerView extends MarkerView {

    private final TextView contentTV;
    private final DecimalFormat df = new DecimalFormat();

    public RadarMarkerView(Context context, int layoutResource) {
        super(context, layoutResource);

        df.setMaximumFractionDigits(1);
        contentTV = findViewById(R.id.contentTV);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        contentTV.setText(String.format("%s/5", df.format(e.getY())));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-((float)getWidth() / 2), - getHeight() - 10);
    }
}
