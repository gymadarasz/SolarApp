package com.kualimecatronica.mexoldroid.util;

/**
 * Created by Angelo on 11/02/2017.
 */

public class GlobalData {

    private static GlobalData instance;

    private float panelReading;
    private float batteryReading;
    private boolean isConnected;
    private boolean showDialog;

    public static synchronized GlobalData getInstance() {
        if (instance == null) instance = new GlobalData();
        return instance;
    }


    public float getPanelReading() {
        return panelReading;
    }

    public void setPanelReading(float panelReading) {
        this.panelReading = panelReading;
    }

    public float getBatteryReading() {
        return batteryReading;
    }

    public void setBatteryReading(float batteryReading) {
        this.batteryReading = batteryReading;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void isConnected(boolean connected) {
        isConnected = connected;
    }

    public void dontShowInsufficientDataDialog(boolean showDialog) {
        this.showDialog = showDialog;
    }

    public boolean dontShowInsufficientDataDialog(){
        return showDialog;
    }
}
