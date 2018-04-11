package com.kualimecatronica.mexoldroid.activity.settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;

import com.kualimecatronica.mexoldroid.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by Angelo on 03/01/2017.
 */

public class SettingsActivity extends AppCompatPreferenceActivity implements
        SharedPreferences.OnSharedPreferenceChangeListener, Preference.OnPreferenceClickListener {

    private NumberPickerPreference picker;
    private CheckBoxPreference enabled;
    private DatePickerPreference datePicker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setupActionBar();
        addPreferencesFromResource(R.xml.preference);
        picker = (NumberPickerPreference) findPreference(getString(R.string.key_money));
        /*datePicker = (DatePickerPreference)findPreference(getString(R.string.payment_day_key));
        datePicker.setSummary(getString(R.string.setted_date) + PreferenceManager
                .getDefaultSharedPreferences(this)
                .getString(getString(R.string.payment_day_key), getString(R.string.blank_space)));

        datePicker.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                datePicker.setSummary(getString(R.string.setted_date) + newValue);
                return false;
            }
        });*/
        final DatePickerPreference dp = (DatePickerPreference) findPreference("cfe_pay_day");
        dp.setSummary(getPreferenceScreen().getSharedPreferences()
                .getString("cfe_pay_day", ""));
        dp.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference,Object newValue) {
                //your code to change values.
                dp.setSummary(newValue.toString());
                return true;
            }
        });
        enabled = (CheckBoxPreference) findPreference(getString(R.string.activate_notification));
        enabled.setOnPreferenceClickListener(this);
        picker.setSummary(getString(R.string.setted_limit) + picker.getValue());
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        checkEnabled();

    }

    /**
     * Set up the {@link android.app.ActionBar}, if the API is available.
     */
    private void setupActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            // Show the Up button in the action bar.
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    public void checkEnabled() {
        if (!enabled.isChecked())
            picker.setEnabled(false);
        else picker.setEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) finish();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(getString(R.string.key_money)))
            picker.setSummary("Límite establecido: $" + picker.getValue());

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        checkEnabled();
        return false;
    }
}
