package com.kualimecatronica.mexoldroid.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.kualimecatronica.mexoldroid.R;

/**
 * Created by Angelo on 08/01/2017.
 */
//// TODO: 11/02/2017 RESTART THRIFT WHEN PASSED 2 MONTHS AND COMPUTE THE KW PER HOUR NOT SECONDS
public class CostCalculator {

    private Context context;
    protected float volt;
    protected float current;
    protected float power;
    protected float batteryCharge;
    protected float fee;
    private GlobalData mGlobal;

    private SharedPreferences prefs;

    /*
    *   CIRCUIT VALUES
    */
    protected static final int SOLAR_CELL_VOLT = 24;
    protected static final double SOLAR_CELL_RESISTOR = 2.2577;
    private static final double SOLAR_CELL_CURRENT = SOLAR_CELL_VOLT / SOLAR_CELL_RESISTOR;
    private static final double SOLAR_CELL_POWER = SOLAR_CELL_VOLT * SOLAR_CELL_CURRENT;

    protected static final int BATTERY_VOLT = 12;

    private static final int BITS_ARDUINO_CONVERSION = 1010;
    private static final float ARDUINO_READING_VOLT = 3.3f;
    private static final int ARDUINO_READING_RESISTOR = 10350;
    private static final double ARDUINO_READING_CURRENT = ARDUINO_READING_VOLT / ARDUINO_READING_RESISTOR;
    private static final double ARDUINO_READING_POWER = ARDUINO_READING_VOLT * ARDUINO_READING_CURRENT;

    /*
    *                               CFE Costs
    *   For every kWh consumed this is the price according to the limits below
    */
    public static final double BASIC_CONSUMPTION_RATE = 0.793;
    public static final double BASIC_CONSUMPTION_RATE_PER_MINUTE = BASIC_CONSUMPTION_RATE / 60;
    public static final double BASIC_CONSUMPTION_RATE_PER_SECOND = BASIC_CONSUMPTION_RATE_PER_MINUTE / 60;

    private static final double INTERMEDIATE_CONSUMPTION_RATE = 0.956;
    private static final double SURPLUS_CONSUMPTION_RATE = 2.802;

    private static final int BASIC_LIMIT = 150;
    private static final int INTERMEDIATE_LIMIT = 280;
    private static final int SURPLUS_LIMIT = 500;
    private double rate;

    public CostCalculator(Context context) {
        this.context = context;
        mGlobal = mGlobal.getInstance();
        getPreferenceData();
    }

    public void getPreferenceData() {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    public int getBatteryPorcent(float batteryCharge) {
        return (int) batteryCharge * 100 / BATTERY_VOLT;
    }

    public float getVolt() {
        return mGlobal.getPanelReading() * SOLAR_CELL_VOLT / BITS_ARDUINO_CONVERSION;
    }

    public void setVolt(float volt) {
        this.volt = volt;
    }

    public float getCurrent() {
        return (float) (getVolt() / SOLAR_CELL_RESISTOR);
    }

    public void setCurrent(float current) {
        this.current = current;
    }

    public float getPower() {
        return (getVolt() * getCurrent()) / 1000;
    }

    public void setPower(float power) {
        this.power = power;
    }

    public float getBatteryCharge() {
        return mGlobal.getBatteryReading() * BATTERY_VOLT / BITS_ARDUINO_CONVERSION;
    }

    public void setBatteryCharge(float batteryCharge) {
        this.batteryCharge = batteryCharge;
    }

    public float getFee() {
        return fee;
    }

    public void setFee(float fee) {
        this.fee = fee;
    }

    //// FIXME: 18/02/2017 Here is the bug of showing thrift and the access to it
    public float getThrift() {
        return prefs.getFloat(context.getString(R.string.thrift_key), 0);
    }

    public void setThrift(float thrift) {
        prefs.edit().putFloat(context.getString(R.string.thrift_key), thrift).commit();
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public double getRate() {
        return this.rate;
    }
}
