package com.kualimecatronica.mexoldroid.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;

/**
 * Created by Angelo on 18/02/2017.
 */

public class HourFormat extends Format {


    public static final String TWO_DECIMAL_FORMAT = "#.##";
    public static final String THREE_DECIMAL_FORMAT = "#.###";
    public static final String FOUR_DECIMAL_FORMAT = "#.####";

    @Override
    public StringBuffer format(Object obj, StringBuffer toAppendTo, FieldPosition pos) {
        int i = Math.round(((Number) obj).floatValue());
        return toAppendTo.append(Math.round(((Number) obj).floatValue()) + " hrs");
    }

    @Override
    public Object parseObject(String source, ParsePosition pos) {
        return null;
    }
}