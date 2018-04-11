package com.kualimecatronica.mexoldroid.activity;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.kualimecatronica.mexoldroid.R;
import com.kualimecatronica.mexoldroid.activity.settings.SettingsActivity;
import com.kualimecatronica.mexoldroid.internet.ConnectionThread;
import com.kualimecatronica.mexoldroid.util.UserInteraction;

import java.io.IOException;

/**
 * Created by Angelo on 22/12/2016.
 */

public class MainActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private Button mConnectButton;
    private UserInteraction mUi;
    private static SharedPreferences mPrefs;
    private SharedPreferences.Editor mEditor;
    private LinearLayout mContactUsLayout;
    private String mUrl = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mUi = new UserInteraction(this);
        mUrl = getString(R.string.kuali_url);
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);//getSharedPreferences(getString(R.string.keep_running), 0);
        mEditor = mPrefs.edit();
        if (mPrefs.getFloat("saving", 0) == 0) mEditor.putFloat("saving", 0);
        mEditor.commit();
        if (getIntent().getBooleanExtra("EXIT", false))
            finish();

        mConnectButton = (Button) findViewById(R.id.connect_button);
        mContactUsLayout = (LinearLayout) findViewById(R.id.contact_us);
        final Intent intent = new Intent(this, DataActivity.class);
        mConnectButton.setOnClickListener(this);
        mContactUsLayout.setOnClickListener(this);
        mContactUsLayout.setOnTouchListener(this);
        verifyVibrationPermission(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) ;
        startActivity(new Intent(this, SettingsActivity.class));
        return true;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.connect_button:
                verifyInternetPermissions(this);
                new TestConnection().execute();
                break;
            case R.id.contact_us:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(mUrl));
                startActivity(i);
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                ObjectAnimator anim = ObjectAnimator.ofFloat(v, "alpha", 0f, 1f);
                anim.setDuration(2000);
                anim.start();
                break;
        }
        return false;
    }

    private static final int REQUEST_INTERNET = 1;
    private static String[] PERMISSIONS_INTERNET = {Manifest.permission.INTERNET};

    /**
     * Checks if the app has permission to write to device storage
     * <p>
     * If the app does not has permission then the user will be prompted to grant permissions
     *
     * @param activity
     */
    public static void verifyInternetPermissions(Activity activity) {
        // Check if we have write permission
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // We don't have permission so prompt the user
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_INTERNET,
                    REQUEST_INTERNET
            );
        }
    }

    public void verifyVibrationPermission(Activity activity) {
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.VIBRATE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.VIBRATE}, 1);
        }
    }


    public class TestConnection extends AsyncTask<Void, Integer, Integer> {

        private ProgressDialog mProgressDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog = new ProgressDialog(MainActivity.this);
            mProgressDialog.setMessage(MainActivity.this.getString(R.string.connecting_to_solar_panel));
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setCancelable(true);
            mProgressDialog.show();
        }

        @Override
        protected Integer doInBackground(Void... params) {
            int port;
            try {
                port = Integer.parseInt(mPrefs.getString("port_key", "10001"));
            } catch (NumberFormatException e) {
                e.printStackTrace();
                port = 10001;
            }
            ConnectionThread testConnection =
                    new ConnectionThread(
                            mPrefs.getString("ip_key", ""),
                            port
                    );
            try {
                if (testConnection.testConnection(MainActivity.this, true)) {
                    startActivity(new Intent(MainActivity.this, DataActivity.class));
                 /*If the connection was successful*/

                    //show connection succeed message to user
                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mUi.showToast(R.string.connection_to_server_success);
                        }
                    });
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            mProgressDialog.dismiss();
        }
    }
}
