package com.kualimecatronica.mexoldroid.util;

import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Angelo on 18/02/2017.
 */

public class TimestampFormat extends Format{

    public final String DOMAIN_LABEL_TIMESTAMP_FORMAT = "MM/yyyy";
    private SimpleDateFormat dateFormat = new SimpleDateFormat(DOMAIN_LABEL_TIMESTAMP_FORMAT);

    @Override
    public StringBuffer format(Object obj, StringBuffer stringBuffer, FieldPosition fieldPosition) {
        // because our timestamps are in seconds and SimpleDateFormat expects milliseconds
        // we multiply our timestamp by 1000:
        long timestamp = ((Number) obj).longValue();
        Date date = new Date(timestamp);
        return dateFormat.format(date, stringBuffer, fieldPosition);
    }

    @Override
    public Object parseObject(String s, ParsePosition parsePosition) {
        return null;
    }
}
