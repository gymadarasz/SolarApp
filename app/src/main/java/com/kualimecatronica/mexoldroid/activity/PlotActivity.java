package com.kualimecatronica.mexoldroid.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.graphics.*;
import android.view.Display;

import com.androidplot.xy.SimpleXYSeries;
import com.androidplot.xy.XYSeries;
import com.androidplot.xy.*;

import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.Format;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * A simple XYPlot
 */
import com.kualimecatronica.mexoldroid.R;
import com.kualimecatronica.mexoldroid.database.Database;
import com.kualimecatronica.mexoldroid.internet.ConnectionThread;
import com.kualimecatronica.mexoldroid.util.GlobalData;
import com.kualimecatronica.mexoldroid.util.HourFormat;
import com.kualimecatronica.mexoldroid.util.TimestampFormat;
import com.kualimecatronica.mexoldroid.util.UserInteraction;

import static com.kualimecatronica.mexoldroid.internet.ConnectionThread.DATE_FORMAT;

/**
 * Created by Angelo on 26/12/2016.
 */

public class PlotActivity extends AppCompatActivity implements DialogInterface.OnClickListener {

    /*
    *   Variable declaration for every plot, ranges and domains (numbers), also the series
    *   and the formatter of the series (colors)
    *
    */

    private XYPlot mKwByHourPlot;
    private XYPlot mKwByMonthPlot;
    private Number[] mHourNumbers;
    private Number[] mKwByHourNumbers;
    private Number[] mMonthTimestamps;
    private Number[] mKwByMonthNumbers;
    private XYSeries mKwByHourSerie;
    private XYSeries mKwByMonthSerie;
    private LineAndPointFormatter mKwByHourFormatter;
    private LineAndPointFormatter mKwByMonthFormatter;
    private UserInteraction mUi;

    /*
    * Variable for database
    */
    private Database mDb;


    /**
     * @param savedInstanceState the instance for start this activity inherited from AppCompatActivity
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plot);
        mKwByHourPlot = (XYPlot) findViewById(R.id.date_time);
        mKwByMonthPlot = (XYPlot) findViewById(R.id.date);
        mUi = new UserInteraction(this);
        //// TODO: 19/02/2017 ADD THIS DIALOG TO THE MAIN, THIS MUST BE BEFORE THE ONCREATE OF ACTIVITY
        getNumbersFromDatabase(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
        configureSeries();
        addSeriesToPlot();
        addLineLabelFormat();
        final GlobalData globalData = GlobalData.getInstance();
        if (mKwByMonthNumbers.length == 1)
            if (!globalData.dontShowInsufficientDataDialog())
                mUi.showAlertDialog(
                    true,
                    R.string.insufficient_data_in_plot_dialog_title,
                    R.string.insufficient_data_in_plot_dialog_message,
                    R.string.accept_dialog_button, R.string.dont_show_again_dialog, null,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            globalData.dontShowInsufficientDataDialog(true);
                        }
                    }, true
            );

    }

    public void addLineLabelFormat() {
        mKwByHourPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT)
                .setFormat(new DecimalFormat(HourFormat.TWO_DECIMAL_FORMAT));
        mKwByHourPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
                .setFormat(new HourFormat());
        mKwByMonthPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.LEFT)
                .setFormat(new DecimalFormat(HourFormat.THREE_DECIMAL_FORMAT));
        mKwByMonthPlot.getGraph().getLineLabelStyle(XYGraphWidget.Edge.BOTTOM)
                .setFormat(new TimestampFormat());
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        int height = size.y;
        mKwByMonthPlot.setRangeStep(StepMode.INCREMENT_BY_PIXELS, height / 10);
        mKwByMonthPlot.setDomainStep(StepMode.INCREMENT_BY_PIXELS, width / 1.5);

    }

    public void addSeriesToPlot() {
        mKwByHourPlot.addSeries(mKwByHourSerie, mKwByHourFormatter);
        mKwByMonthPlot.addSeries(mKwByMonthSerie, mKwByMonthFormatter);
    }

    public void configureSeries() {
        mKwByHourSerie = new SimpleXYSeries(
                Arrays.asList(mKwByHourNumbers),
                SimpleXYSeries.ArrayFormat.Y_VALS_ONLY,
                getString(R.string.plot_serie_label_daily)
        );
        mKwByMonthSerie = new SimpleXYSeries(
                Arrays.asList(mMonthTimestamps),
                Arrays.asList(mKwByMonthNumbers),
                getString(R.string.plot_serie_label_month)
        );
        mKwByHourFormatter = new LineAndPointFormatter(
                Color.rgb(0, 0, 0), null,
                Color.argb(255, 255, 200, 150), null);
        mKwByMonthFormatter = new LineAndPointFormatter(
                Color.rgb(0, 0, 0), null,
                Color.argb(255, 255, 150, 50), null);
        mKwByHourFormatter.setInterpolationParams(
                new CatmullRomInterpolator.Params(200, CatmullRomInterpolator.Type.Centripetal));
        mKwByMonthFormatter.setInterpolationParams(
                new CatmullRomInterpolator.Params(10, CatmullRomInterpolator.Type.Centripetal));
    }

    /**
     * @param date the actual date for obtain the actual data
     */
    private void getNumbersFromDatabase(String date) {
        /*
        *   Get access to database
        */
        mDb = new Database(this);
        mDb.setContext(this);
        mDb.open();
        /*
        *   Get arrays from database queries, every query generate a multi-dimensional
        *   array one for every column (time/kw registered)
        */
        this.mHourNumbers = mDb.getDayKW(date)[0];
        this.mKwByHourNumbers = mDb.getDayKW(date)[1];

        this.mMonthTimestamps = mDb.getMonthAverage()[0];
        this.mKwByMonthNumbers = mDb.getMonthAverage()[1];

        /*  Close database
        * */
        mDb.close();
    }

    @Override
    public void onClick(DialogInterface dialog, int which) {
        PlotActivity.this.finish();
    }
}
