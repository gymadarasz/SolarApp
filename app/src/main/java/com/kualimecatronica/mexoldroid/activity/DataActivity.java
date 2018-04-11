package com.kualimecatronica.mexoldroid.activity;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kualimecatronica.mexoldroid.R;
import com.kualimecatronica.mexoldroid.activity.settings.SettingsActivity;
import com.kualimecatronica.mexoldroid.database.Database;
import com.kualimecatronica.mexoldroid.internet.ConnectionThread;
import com.kualimecatronica.mexoldroid.util.UserInteraction;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Angelo on 22/12/2016.
 */

public class DataActivity extends AppCompatActivity implements View.OnClickListener {


    private Button mPlotButton, mGetOutButton;
    public Button mDisconnectButton;
    protected TextView mSavedMoney;
    protected TextView mVoltIndicator;
    protected TextView mCurrentIndicator;
    protected TextView mWattsIndicator;
    protected TextView mBatteryPorcent;
    protected ProgressBar mBatteryProgressbar;
    private static SharedPreferences mPrefs;
    private ConnectionThread mConnectionThread;
    private final String EXIT_FLAG = "EXIT";
    public final String DATE_FORMAT = "yyyy-MM-dd";
    private UserInteraction mUserInteraction;

    private boolean mStopped;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);

        mSavedMoney = (TextView) findViewById(R.id.saved_money);
        mVoltIndicator = (TextView) findViewById(R.id.volt);
        mCurrentIndicator = (TextView) findViewById(R.id.current);
        mWattsIndicator = (TextView) findViewById(R.id.watts);
        mBatteryPorcent = (TextView) findViewById(R.id.battery_porcent);
        mPlotButton = (Button) findViewById(R.id.plot_button);
        mGetOutButton = (Button) findViewById(R.id.get_out_button);
        mDisconnectButton = (Button) findViewById(R.id.disconnect_button);
        mBatteryProgressbar = (ProgressBar) findViewById(R.id.progressbar_battery);

        mGetOutButton.setOnClickListener(this);
        mPlotButton.setOnClickListener(this);
        mDisconnectButton.setOnClickListener(this);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        mUserInteraction = new UserInteraction(this);
        startConnection();
    }

    /*public void fillDatabase(String date, int amountOfData) {
        Database db = new Database(this);
        db.setContext(this);
        SQLiteDatabase database = db.getWritableDatabase();
        db.fillDatabase(database, date, amountOfData);
        db.close();
    }*/

    /**
     * @throws ParseException
     */
/*
        es la primera vez que se corre? establece la fecha de corrida como la inicial
        luego guarda una variable que indique que ya se corrio (true)

        para la siguiente vez se comprueba si no se ha corrido y hace lo anterior
        si ya se corrio entonces comprueba la fecha, si la fecha guardada más dos meses
        (si la opcion de 2 meses esta si no, establecer la del usuario)
        es la fecha actual, pon en ceros el contador de dinero ahorrado y establece la
        nueva fecha como fecha de inicio (fecha guardada).
        */

    public void startConnection() {
        //(dias*((mWattsIndicator/1000))*horas)
        int port;
        try {
            port = Integer.parseInt(mPrefs.getString("port_key", "10001"));
        } catch (NumberFormatException e) {
            e.printStackTrace();
            port = 10001;
        }
        mConnectionThread = new ConnectionThread(
                this, mPrefs.getString("ip_key", ""), port);
        mConnectionThread.setUIComponents(
                mSavedMoney,
                mVoltIndicator,
                mCurrentIndicator,
                mWattsIndicator,
                mBatteryPorcent,
                mBatteryProgressbar
        );
        mConnectionThread.isKeepRunning(true);
        mConnectionThread.start();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.get_out_button:
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra(EXIT_FLAG, true);
                startActivity(intent);
                break;
            case R.id.plot_button:
                if (!isEmptyDatabase()) startActivity(new Intent(this, PlotActivity.class));
                else mUserInteraction.showSnackbar(R.string.empty_database);
                break;
            case R.id.disconnect_button:
                if (mDisconnectButton.getText().equals("Iniciar")) {
                    mDisconnectButton.setText("Detener");
                    mStopped = false;
                    mConnectionThread.isKeepRunning(true);
                    startConnection();
                } else {
                    mStopped = true;
                    mDisconnectButton.setText("Iniciar");
                    mConnectionThread.isKeepRunning(false);
                }
                break;
        }
    }

    public boolean isEmptyDatabase() {
        Database db = new Database(this);
        db.setContext(this);
        db.open();
        if (db.getAllData().getCount() <= 2 &&
                db.getDayKW(new SimpleDateFormat(DATE_FORMAT)
                        .format(new Date())).length <= 3 &&
                db.getMonthAverage().length <= 3)
            return true;
        db.close();
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(mStopped) mConnectionThread.isKeepRunning(false);
        if (mConnectionThread.isKeepRunning())
            mDisconnectButton.setText("Detener");

        //isKeepRunning(false);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings)
            startActivity(new Intent(this, SettingsActivity.class));
        return super.onOptionsItemSelected(item);
    }

}
