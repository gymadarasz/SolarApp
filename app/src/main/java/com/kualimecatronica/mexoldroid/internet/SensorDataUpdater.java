package com.kualimecatronica.mexoldroid.internet;

import android.util.Log;

import com.kualimecatronica.mexoldroid.util.GlobalData;
//// TODO: 11/02/2017 TEST MESSAGES RECEIVED
public class SensorDataUpdater {

    private StringBuilder message;
    private GlobalData global;

    public SensorDataUpdater() {message = new StringBuilder();}

    public void debugMessage(String data) {
        message.append(data);
        Log.d("data",data);
        int endOfLineIndex = message.indexOf("~");// determine the end-of-line

        if (endOfLineIndex > 0) { // make sure there data before ~
            String dataInPrint = message.substring(0, endOfLineIndex);// extract string

            if (message.charAt(0) == '#') {//if it starts with # we know it is what we are looking for
                int[] endOfValues = new int[2];//determine when a read value is finish
                int j = 0;
                for (int i = 1; i < dataInPrint.length(); i++) {
                    if (dataInPrint.charAt(i) == '+') {//if find a + save into an array that position
                        try {
                            endOfValues[j] = i;
                        } catch (ArrayIndexOutOfBoundsException e) {
                            e.printStackTrace();
                            return;
                        }
                        j++;
                    }
                }
                try {
                    setData(endOfValues);
                } catch (ArrayIndexOutOfBoundsException e) {
                    e.printStackTrace();
                    return;
                }
            }
            message.delete(0, message.length());//clear all string data
        }
    }
    public void setData(int endOfValues[]) {
        global = global.getInstance();
        global.setPanelReading(
                Float.parseFloat(message.substring(1, endOfValues[0])));
        Log.d("second value",message.substring(endOfValues[0] + 1, endOfValues[1])+"");
        global.setBatteryReading(
                Float.parseFloat(message.substring(endOfValues[0] + 1, /*message.length()-1*/endOfValues[1])));
    }
}