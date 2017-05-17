package br.com.electricapp.electricapp.util;

import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.text.DecimalFormat;

/**
 * Created by Nathalia on 16/05/2017.
 */
public class MyYAxisValueFormatter implements IAxisValueFormatter {

    private DecimalFormat mFormat;

    public MyYAxisValueFormatter() {
        // format values to 2 decimal digit
        mFormat = new DecimalFormat("###,###,##0.00");
    }

    @Override
    public String getFormattedValue(float value, AxisBase axis) {
        // "value" represents the position of the label on the axis (x or y)
        return mFormat.format(value);
    }

    /** this is only needed if numbers are returned, else return 0 */
//    @Override
    public int getDecimalDigits() {
        return 2;
    }
}
