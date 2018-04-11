package com.kualimecatronica.mexoldroid.simulator;

import android.content.Context;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kualimecatronica.mexoldroid.R;
import com.kualimecatronica.mexoldroid.activity.DataActivity;
import com.kualimecatronica.mexoldroid.database.Database;
import com.kualimecatronica.mexoldroid.util.CostCalculator;

import java.text.SimpleDateFormat;

/**
 * Created by Angelo on 08/01/2017.
 */

public class SimulatorThread extends Thread {

    private final Context context;
    private final SimpleDateFormat dateFormat;
    private boolean keepRunning = true;

    private TextView savedMoney, volt, current, watts, batteryPorcent;
    private ProgressBar batteryIndicator;
    private CostCalculator cost;
    private float powerAverage;
    private float powerCounter;
    private int counter;
    public static String format = "yyyy-MM-dd HH:mm";

    private long initialTime;

    private Database mDb;

    public SimulatorThread(Context context, TextView thrift, TextView volt, TextView current,
                           TextView power, ProgressBar battery, TextView batteryIndicator) {
        this.context = context;
        this.savedMoney = thrift;
        this.volt = volt;
        this.current = current;
        this.watts = power;
        this.batteryIndicator = battery;
        this.batteryPorcent = batteryIndicator;
        cost = new CostCalculator(context);
        mDb = new Database(context);
        mDb.setContext(context);
        dateFormat = new SimpleDateFormat(format);
    }

    public void saveData(String date, String kw){
        //mDb.open();
        //mDb.insertData(date, kw);
        mDb.generateValues(date,1500);
        //mDb.close();
    }

    public void updateUI() {
        ((DataActivity) this.context).runOnUiThread(
                new UIUpdaterSimulated(
                        context,
                        savedMoney,
                        volt,
                        current,
                        watts,
                        batteryPorcent,
                        batteryIndicator,
                        cost
                )
        );
    }

    @Override
    public void run() {
        //obtain the initial time of monitoring
        initialTime = System.currentTimeMillis();
        //start the avg
        powerAverage = 0;
        //start the counter for obtain avg
        counter = 0;
        //start infinite loop
        while (isKeepRunning()) {
            //update UI (numbers and bars)
            updateUI();
            //sleep one second (refresh rate)
            try {
                //refresh every second
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //compute avg
            try {
            /*    //Log.d("CALENDAR",Calendar.getInstance().get(Calendar.HOUR)+":"+Calendar.getInstance().get(Calendar.MINUTE)+":"+Calendar.getInstance().get(Calendar.SECOND));
                Log.d("CALENDAR",Calendar.getInstance().get(Calendar.MONTH)+"");
                //if the time of system is more than initial time and 200 milliseconds after
                if (System.currentTimeMillis() - initialTime >= 1000 &&
                        System.currentTimeMillis() - initialTime <= 1020){
                    //powerAverage = 0;
                    //obtain the avg in 1 minute (apply the same for hour)
                    Log.d("1 SECONDS", "el promedio en 1 segundos es:" +powerAverage);
                    //a hour have 3600000000 milliseconds
                    initialTime = System.currentTimeMillis();
                    //saveData(dateFormat.DATE_FORMAT(new Date()), powerAverage+"");

                }
                //// TODO: 12/02/2017 Save the kw/h every hour
                Log.d("AVG", "promedio :" +powerAverage);
                //FOR HOUR IS Calendar.MINUTE == 0
                //if(((int) Calendar.getInstance().get(Calendar.MINUTE)) == 0){
                    /*Save avg to database
                    * currentDateandTime = dateFormat.DATE_FORMAT(new Date());*/
                //};

                powerCounter += Float.parseFloat(watts.getText().toString().replace(context.getString(R.string.power_unit),"").replace(",","."));
                counter++;
                powerAverage = powerCounter / counter;
                //Log.d("avg",powerAverage+"");
            } catch (NumberFormatException ex) {
                ex.printStackTrace();
            }
        }
    }


    public boolean isKeepRunning() {
        return keepRunning;
    }

    public void setKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }
}
