package com.kualimecatronica.mexoldroid.util;

import java.text.DecimalFormat;

/**
 * Created by Angelo on 02/01/2017.
 * A class for convert units
 */
public class UnitConverter {

    private static final float constantConverter = 1000f;
    private DecimalFormat precision;
    private static final String pattern = "0.00";

    /**
     * Instantiate the decimal formatter with constructor
     */
    public UnitConverter(){
        precision = new DecimalFormat(pattern);
    }

    /**
     * @param value the value to convert to milli
     * @return the value converted
     */
    public float convertToMilli(float value) {
        return value * constantConverter;
    }

    /**
     * @param value the value to convert to normal
     * @return the value converted
     */
    public float convertToNormal(float value){
        return value / constantConverter;
    }

    /**
     * @param value the value to DATE_FORMAT to precision established
     * @return the string of value formatted
     */
    public String getValueConvertedToMilli(float value){
        return precision.format(convertToMilli(value));
    }

    /**
     * @param value the value to DATE_FORMAT into the precision established
     *
     * @return
     */
    public String getValueConvertedToNormal(float value){
        return precision.format(convertToNormal(value));
    }


}
