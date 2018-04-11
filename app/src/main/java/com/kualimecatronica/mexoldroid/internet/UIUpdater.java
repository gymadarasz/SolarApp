package com.kualimecatronica.mexoldroid.internet;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kualimecatronica.mexoldroid.R;
import com.kualimecatronica.mexoldroid.util.CostCalculator;
import com.kualimecatronica.mexoldroid.util.HourFormat;

import java.text.DecimalFormat;

/**
 * Created by Angelo on 18/02/2017.
 */

public class UIUpdater implements Runnable{

    private TextView mSavedMoney;
    private TextView mVoltIndicator;
    private TextView mCurrentIndicator;
    private TextView mWattsIndicator;
    private TextView mBatteryPorcent;
    private ProgressBar mBatteryProgressbar;
    private CostCalculator mCost;
    private DecimalFormat mEnergyValuesFormat, mThirftFormat;
    private Context mContext;

    private String mThrift, mVolt, mCurrent, mWatts, mBattery;
    private byte mPorcent;
    private double mRate;


    public UIUpdater(Context mContext, TextView mSavedMoney, TextView mVoltIndicator, TextView mCurrentIndicator,
                     TextView mWattsIndicator, TextView mBatteryPorcent, ProgressBar mBatteryProgressbar) {
        this.mSavedMoney = mSavedMoney;
        this.mVoltIndicator = mVoltIndicator;
        this.mCurrentIndicator = mCurrentIndicator;
        this.mWattsIndicator = mWattsIndicator;
        this.mBatteryPorcent = mBatteryPorcent;
        this.mBatteryProgressbar = mBatteryProgressbar;
        this.mContext = mContext;
        mEnergyValuesFormat = new DecimalFormat(HourFormat.TWO_DECIMAL_FORMAT);
        mThirftFormat = new DecimalFormat(HourFormat.FOUR_DECIMAL_FORMAT);
        mCost = new CostCalculator(mContext);
    }

    //// FIXME: 18/02/2017 ERROR ACCESSING TO THE DEFAULT PREFERENCES IN THRIFT
    private void calculateValues(double rate) {
        mCost.setRate(rate);
        mCost.setThrift((float) (mCost.getThrift() + (mCost.getPower() * mCost.getRate())));
        mThrift = mThirftFormat.format(mCost.getThrift());
        mVolt = mEnergyValuesFormat.format(mCost.getVolt()) +
                mContext.getString(R.string.volt_unit);
        mCurrent = mEnergyValuesFormat.format(mCost.getCurrent()) +
                mContext.getString(R.string.current_unit);
        mWatts = mEnergyValuesFormat.format(mCost.getPower()) +
                mContext.getString(R.string.power_unit);
        mPorcent = (byte) mCost.getBatteryPorcent(mCost.getBatteryCharge());
        mBattery = mPorcent +
                mContext.getString(R.string.percent_symbol);
    }

    public void setDataToUI(double rate) {
        calculateValues(rate);
        mSavedMoney.setText(mThrift);
        mVoltIndicator.setText(mVolt);
        mCurrentIndicator.setText(mCurrent);
        mWattsIndicator.setText(mWatts);
        mBatteryPorcent.setText(mBattery);
        mBatteryProgressbar.setProgress(mPorcent);
    }
    public void setRate(double rate){
        this.mRate = rate;
    }

    public double getRate() {
        return mRate;
    }

    @Override
    public void run() {
        setDataToUI(getRate());
    }
}
