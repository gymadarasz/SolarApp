package com.kualimecatronica.mexoldroid.simulator;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kualimecatronica.mexoldroid.R;
import com.kualimecatronica.mexoldroid.util.CostCalculator;

import java.text.DecimalFormat;


/**
 * Created by Angelo on 08/01/2017.
 */

public class UIUpdaterSimulated implements Runnable {
    private CostCalculator cost;
    private Context mContext;
    private DecimalFormat format;
    private final String DECIMAL_FORMAT = "#.##";
    DataSimulator sim;
    protected TextView mSavedMoney;
    protected TextView mVoltIndicator;
    protected TextView mCurrentIndicator;
    protected TextView mWattsIndicator;
    protected TextView mBatteryPorcent;
    protected ProgressBar mBatteryProgressbar;


    public UIUpdaterSimulated(Context mContext, TextView mSavedMoney, TextView mVoltIndicator, TextView mCurrentIndicator,
                              TextView mWattsIndicator, TextView mBatteryPorcent, ProgressBar mBatteryProgressbar, CostCalculator cost) {
        this.mSavedMoney = mSavedMoney;
        this.mVoltIndicator = mVoltIndicator;
        this.mCurrentIndicator = mCurrentIndicator;
        this.mWattsIndicator = mWattsIndicator;
        this.mBatteryPorcent = mBatteryPorcent;
        this.mBatteryProgressbar = mBatteryProgressbar;
        this.cost = cost;
        sim = new DataSimulator(mContext);
        format = new DecimalFormat(DECIMAL_FORMAT);
        this.mContext = mContext;
    }

    @Override
    public void run() {
        //sim.simulateValues(cost.BASIC_CONSUMPTION_RATE_PER_SECOND);
        setData(sim, true);
    }

    public void setData(CostCalculator cost, boolean simulated) {
        if (simulated) sim.simulateValues(cost.BASIC_CONSUMPTION_RATE_PER_SECOND);
        mSavedMoney.setText(format.format(cost.getThrift()));
        mVoltIndicator.setText(format.format(cost.getVolt()) + mContext.getString(R.string.volt_unit));
        mCurrentIndicator.setText(format.format(cost.getCurrent()) + mContext.getString(R.string.current_unit));
        mWattsIndicator.setText(format.format(cost.getPower()) + mContext.getString(R.string.power_unit));
        mBatteryProgressbar.setProgress(cost.getBatteryPorcent(cost.getBatteryCharge()));
        mBatteryPorcent.setText(cost.getBatteryPorcent(cost.getBatteryCharge()) + mContext.getString(R.string.percent_symbol));
    }
}
