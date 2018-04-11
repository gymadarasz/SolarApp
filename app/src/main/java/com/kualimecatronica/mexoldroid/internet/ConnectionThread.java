package com.kualimecatronica.mexoldroid.internet;

import android.app.Notification;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.kualimecatronica.mexoldroid.R;
import com.kualimecatronica.mexoldroid.activity.DataActivity;
import com.kualimecatronica.mexoldroid.activity.settings.SettingsActivity;
import com.kualimecatronica.mexoldroid.database.Database;
import com.kualimecatronica.mexoldroid.util.CostCalculator;
import com.kualimecatronica.mexoldroid.util.GlobalData;
import com.kualimecatronica.mexoldroid.util.UserInteraction;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Created by Angelo on 10/02/2017.
 */

public class ConnectionThread extends Thread {
    private Context mContext;
    private String mIp;
    private int mPort;
    private Socket mSocket;
    private BufferedReader mInput;
    private DataActivity mDataActivity;
    private SensorDataUpdater mUpdater;
    private GlobalData mGlobal;
    private UIUpdater mUiUpdater;


    private TextView mSavedMoney;
    private TextView mVoltIndicator;
    private TextView mCurrentIndicator;
    private TextView mWattsIndicator;
    private TextView mBatteryPorcent;
    private ProgressBar mBatteryProgressbar;
    private float mPowerAverage;
    private float mCounter;
    private float mPowerCounter;
    public static String DATE_FORMAT = "yyyy-MM-dd";
    public static String DATE_TIME_FORMAT = "yyyy-MM-dd HH:mm";

    private Database mDb;
    private SharedPreferences prefs;
    private UserInteraction mUserInteraction;
    private boolean keepRunning;
    private MediaPlayer mMediaPlayer;

    private SimpleDateFormat mFormat;
    private SharedPreferences mPrefs;
    private static final String DEFAULT_DATE = "2017-01-01";
    private static final String DEFAULT_MONTH = "2";


    /**
     * @param context the context of the app
     * @param ip      the ip to connect
     * @param port    the port to connect
     */
    public ConnectionThread(Context context, String ip, int port) {
        this.mContext = context;
        this.mDataActivity = (DataActivity) context;
        this.mIp = ip;
        this.mPort = port;
        mUpdater = new SensorDataUpdater();
        mGlobal = GlobalData.getInstance();
        mDb = new Database(context);
        mDb.setContext(context);
        mUserInteraction = new UserInteraction(context);
        //mMediaPlayer = new MediaPlayer();
        //mMediaPlayer = MediaPlayer.create(mContext, R.raw.coins_2);
        //mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(context);
    }

    /**
     * @param ip to connect
     * @param port  to connect
     */
    public ConnectionThread(String ip, int port) {
        this.mIp = ip;
        this.mPort = port;
    }

    /**
     * @param mSavedMoney the thrift textview
     * @param mVoltIndicator the volt textview
     * @param mCurrentIndicator the current textview
     * @param mWattsIndicator the power textview
     * @param mBatteryPorcent the battery textview
     * @param mBatteryProgressbar the battery progressbar
     */
    public void setUIComponents(TextView mSavedMoney, TextView mVoltIndicator,
                                TextView mCurrentIndicator, TextView mWattsIndicator,
                                TextView mBatteryPorcent, ProgressBar mBatteryProgressbar) {
        this.mSavedMoney = mSavedMoney;
        this.mVoltIndicator = mVoltIndicator;
        this.mCurrentIndicator = mCurrentIndicator;
        this.mWattsIndicator = mWattsIndicator;
        this.mBatteryPorcent = mBatteryPorcent;
        this.mBatteryProgressbar = mBatteryProgressbar;
    }


    /**
     * @param activity the activity to show toast
     * @param isMainActivity if is testing from mainactivity
     * @return if connection was success
     * @throws IOException for internet socket
     */
    public boolean testConnection(AppCompatActivity activity, boolean isMainActivity) throws IOException {
        mGlobal = mGlobal.getInstance();
        /*Try the connection*/
        try {
            mSocket = new Socket(mIp, mPort);
            mSocket.setSoTimeout(3000);
        } catch (ConnectException ce) {
            ce.printStackTrace();
            //if the exception contains some of this errors, send the notification
            if (ce.toString().contains(activity.getString(
                    R.string.network_unreachable_exception)
            )) {
                notifyConnectionError(ConnectionStatus.INTERNET_DISCONNECTED, activity);
                isKeepRunning(false);
                mGlobal.isConnected(false);
                if (!isMainActivity) showDisconnectedState();
            } else if (ce.toString().contains(activity.getString(
                    R.string.no_route_to_host_exception)
            )) {
                notifyConnectionError(ConnectionStatus.NO_ROUTE_TO_HOST, activity);
                mGlobal.isConnected(false);
                isKeepRunning(false);
                if (!isMainActivity) showDisconnectedState();
            } else if (ce.toString().contains(activity.getString(
                    R.string.connection_refused_exception))) {
                notifyConnectionError(ConnectionStatus.CANNOT_CONNECT, activity);
                mGlobal.isConnected(false);
                isKeepRunning(false);
            }
            ce.printStackTrace();
            Log.e("error", ce + "");
            if (mSocket != null) mSocket.close();
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /**
     * Method to run while app is open
     */
    @Override
    public void run() {
        try {
            mPowerAverage = 0;
            //start the mCounter for obtain avg
            mCounter = 0;
            //start infinite loop
            while (isKeepRunning()) {
                checkBimester();
                if (testConnection(mDataActivity, false)) {
                    try {
                        Thread.sleep(1000);
                        mGlobal.isConnected(true);
                        //get the input from Arduino
                        mInput = new BufferedReader(new InputStreamReader(mSocket.getInputStream()));
                        /*
                        *   Here we going to debug the message from Arduino
                        */
                        //Log.d("DATA", mInput.readLine());
                        mUpdater.debugMessage(mInput.readLine());
                        //mUpdater.debugMessage("#" + Math.floor((Math.random() * 1024) + 1) + "+" + Math.floor((Math.random() * 1024) + 1) + "+~");

                        updateUI(CostCalculator.BASIC_CONSUMPTION_RATE_PER_SECOND);
                        calculateKwH();
                        checkThrift();
                    } catch (Exception e) {
                        //if something went wrong notify to user almost the reason is
                        //the connection was lost
                        mInput.close();
                        mSocket.close();
                        e.printStackTrace();
                        notifyConnectionError(ConnectionStatus.LOST, mDataActivity);
                        //break;
                    }
                }
            }
            //} catch (ConnectException ce) {
            //  ce.printStackTrace();
            //notifyConnectionError(ConnectionStatus.IP_PORT_ERROR);
        } catch (Exception e) {
            notifyConnectionError(ConnectionStatus.LOST, mDataActivity);
            e.printStackTrace();
        }
    }


    /**
     * Check the limit of thrift (in settings) for launch notification
     */
    private void checkThrift() {
        prefs = PreferenceManager.getDefaultSharedPreferences(mContext);
        float thrift = prefs.getFloat(mContext.getString(R.string.thrift_key), 0);
        float limit = prefs.getInt(mContext.getString(R.string.key_money), 0);
        if (thrift >= limit && thrift <= limit + 0.0001)
            if (prefs.getBoolean(mContext.getString(R.string.activate_notification), false))
                showNotification();
        //Log.d("THRIFT AND LIMIT",thrift+","+limit);

    }

    /**
     * Show the thrift notification
     */
    private void showNotification() {
        Uri sound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                + "://" + mContext.getPackageName() + "/raw/coins_2");
        //if (!mMediaPlayer.isPlaying()) mMediaPlayer.start();
        ((Vibrator) mContext.getSystemService(Context.VIBRATOR_SERVICE)).vibrate(100);
        mUserInteraction
                .getNotificationManager()
                .notify(
                        UserInteraction.SAVING,
                        mUserInteraction.createNotification(
                                R.string.saving_notification_title,
                                mContext.getString(R.string.saving_notification_msg).replace("%", mSavedMoney.getText().toString()),
                                android.R.drawable.ic_dialog_info,
                                SettingsActivity.class,
                                SettingsActivity.class,
                                Notification.FLAG_AUTO_CANCEL,
                                sound
                        ));
    }

    /**
     * If passed one hour save to database, also calculate the kw/s average
     */
    public void calculateKwH() {
        if ((Calendar.getInstance().get(Calendar.MINUTE)) == 0) {
            saveKwToDatabase();
            mPowerCounter = 0;
            mCounter = 0;
        }

        mPowerCounter += debugValue(
                mWattsIndicator.getText().toString(),
                mContext.getString(R.string.power_unit)
        );
        mCounter++;
        mPowerAverage = mPowerCounter / mCounter;

    }

    /**
     * @param originalValue the value to debug
     * @param valueToReplace the value to replace
     * @return
     */
    public Float debugValue(String originalValue, String valueToReplace) {
        return Float.parseFloat(
                originalValue
                        .replace(
                                valueToReplace,
                                mContext.getString(R.string.blank_space))
                        .replace(
                                mContext.getString(R.string.comma),
                                mContext.getString(R.string.point)
                        )
        );
    }

    /**
     * Save data to database
     */
    private void saveKwToDatabase() {
        mDb.open();
        mDb.insertData(
                new SimpleDateFormat(DATE_TIME_FORMAT).format(new Date()),
                String.valueOf(mPowerAverage));
        mDb.close();
    }


    /**
     * @param rate rate for calculate energy cost
     */
    public void updateUI(double rate) {
        UIUpdater updater = new UIUpdater(mContext,
                mSavedMoney,
                mVoltIndicator,
                mCurrentIndicator,
                mWattsIndicator,
                mBatteryPorcent,
                mBatteryProgressbar);
        updater.setRate(rate);
        mDataActivity.runOnUiThread(updater);
    }

    /**
     * @param status the status to evaluate
     * @param activity the activity for show toast
     */
    public void notifyConnectionError(final ConnectionStatus status, AppCompatActivity activity) {
        mGlobal = GlobalData.getInstance();
        mUserInteraction = new UserInteraction(activity);
        mGlobal.isConnected(false);

        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //mDataActivity.notification.getNotificationManager().cancel(NotificationHelper.RECEIVING_DATA);
                switch (status) {
                    case NO_ROUTE_TO_HOST:
                        mUserInteraction.showSnackbar(R.string.no_route_to_host);
                        break;
                    case LOST:
                        mUserInteraction.showSnackbar(R.string.conection_lost);
                        break;
                    case IP_PORT_ERROR:
                        mUserInteraction.showSnackbar(R.string.ip_port_error);
                        break;
                    case INPUT_ERROR:
                        mUserInteraction.showSnackbar(R.string.input_error);
                        break;
                    case INTERNET_DISCONNECTED:
                        mUserInteraction.showSnackbar(R.string.internet_disconnected);
                        break;
                    case CANNOT_CONNECT:
                        mUserInteraction.showSnackbar(R.string.connection_refused);
                        break;
                }
            }
        });
    }

    /**
     * Check if the actual date is the limit date established by user and reset values
     * @throws ParseException for date conversion
     */
    private void checkBimester() throws ParseException {
        /*
        * Get the actual date
        * */
        String actualDate;
        mFormat = new SimpleDateFormat(DATE_FORMAT);
        actualDate = mFormat.format(new Date());
        /*
        * If is the first time of running app save the date as the initial date
        * */

            /*
            * Else sum 60 days to saved date and compare with the actual date
            * */
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(mFormat.parse(mPrefs.getString(mContext.getString(R.string.payment_day_key), DEFAULT_DATE)));
        calendar.add(Calendar.MONTH,
                Integer.parseInt(mPrefs.getString(mContext.getString(R.string.update_pref), DEFAULT_MONTH)));  // number of days to add
        if (actualDate.equals(mFormat.format(calendar.getTime()))) {
                /*
                * If the actual date is the saved date more the 60 days then restore data
                * */
            mPrefs.edit().putFloat(mContext.getString(R.string.thrift), 0).apply();
            mPrefs.edit().putString(mContext.getString(R.string.payment_day_key),
                    mFormat.format(calendar.getTime())).apply();
            mUserInteraction.showSnackbar(R.string.data_restored);
        }
        Log.d("RESTORE DATA", actualDate + ", " + mFormat.format(calendar.getTime()));

    }

    /**
     * Show a disconnected state in UI
     */
    public void showDisconnectedState() {
        mDataActivity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mDataActivity.mDisconnectButton.setText("Iniciar");
            }
        });
    }

    /**
     * @param keepRunning setter for keep running thread
     */
    public void isKeepRunning(boolean keepRunning) {
        this.keepRunning = keepRunning;
    }


    /**
     * @return if the thread is running
     */
    public boolean isKeepRunning() {
        return keepRunning;
    }
}
