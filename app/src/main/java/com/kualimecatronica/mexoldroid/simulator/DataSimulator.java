package com.kualimecatronica.mexoldroid.simulator;

import android.content.Context;
import android.util.Log;

import com.kualimecatronica.mexoldroid.util.CostCalculator;

/**
 * Created by Angelo on 11/02/2017.
 */

public class DataSimulator extends CostCalculator {

    public DataSimulator(Context context) {
        super(context);
    }

    public void simulateValues(double rate) {
        setVolt(getSimulatedVolt());
        setCurrent(getSimulatedCurrent(getVolt()));
        setPower(getSimulatedPower(getCurrent(), getVolt()));
        getPreferenceData();
        //setThrift((float) (getThrift() + (getPower() * rate)));
       // Log.d("THRIFT", (getPower()*rate+""));
        setBatteryCharge(getSimulatedBatteryCharge());
    }


    public float getSimulatedVolt() {
        return (float) ((Math.random() * SOLAR_CELL_VOLT) + 1);
    }

    public float getSimulatedBatteryCharge() {
        return (float) ((Math.random() * BATTERY_VOLT) + 1);
    }

    public float getSimulatedCurrent(float volt) {
        return (float) (/*
                (
                        (volt / ARDUINO_READING_RESISTOR) * SOLAR_CELL_CURRENT
                ) / ARDUINO_READING_CURRENT*/
                volt / SOLAR_CELL_RESISTOR
        );
    }

    public float getSimulatedPower(float current, float volt) {
        return /*((current * volt * SOLAR_CELL_POWER)
                / ARDUINO_READING_POWER) / 1000*/
                current * volt/1000;
    }

    @Override
    public float getVolt() {
        return volt;
    }

    @Override
    public float getCurrent() {
        return current;
    }

    @Override
    public float getPower() {
        return power;
    }

    @Override
    public float getBatteryCharge() {
        return batteryCharge;
    }
}
