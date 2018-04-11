package com.kualimecatronica.mexoldroid.database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Angelo on 26/12/2016.
 */


public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "intake.db";
    private static final String TABLE_NAME = "CONSUMPTION";
    private static final String DATE_COLUMN = "DATE";
    private static final String KW_COLUMN = "KW";
    private static final String CREATE_TABLE =
            "create table " + TABLE_NAME
                    + " ("
                    + DATE_COLUMN + " date primary key not null,"
                    + KW_COLUMN + " numeric" +
                    ");";
    private static final String[] columns = {DATE_COLUMN, KW_COLUMN};
    private SQLiteDatabase db;
    private static final String ALL_DATA_QUERY =
            "select * from " + TABLE_NAME;
    private ContentValues values;
    private Context context;

    public void setContext(Context context) {
        this.context = context;
    }

    public Database(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void open() throws SQLException {
        db = new Database(context).getWritableDatabase();
    }

    public Cursor getAllData() {
        open();
        return db.rawQuery(ALL_DATA_QUERY, null);
    }

    public void insertData(String date, String kw) {

        values = new ContentValues();

        values.put(this.DATE_COLUMN, date);
        values.put(this.KW_COLUMN, kw);

        db.insert(this.TABLE_NAME, null, values);
    }

    public String[] generateValues(String startDate, int amount) {
        //2017-02-14 11:00
        int year, month, day, hour, kw;
        year = Integer.parseInt(startDate.substring(0, 4));
        month = Integer.parseInt(startDate.substring(5, 7));
        day = Integer.parseInt(startDate.substring(8, 10));
        hour = Integer.parseInt(startDate.substring(11, 13));
        String[] dates = new String[amount];
        for (int i = 0; i < amount; i++) {
            year = (month == 1) ? ++year : year;
            if (day == 31) {
                month = month >= 13 ? 1 : ++month;
                day = 1;
            }
            if (hour == 23) {
                day = day >= 31 ? 1 : ++day;
                hour = 0;
            } else hour++;
            if (day != 31) dates[i] = getDateTimeFormatted(year, month, day, hour);
        }
        return dates;
    }

    public Number[][] getDayKW(String date) {
        Number[] hour, kw;
        Cursor cursor = db.rawQuery("select strftime('%H',date), kw from consumption where date like '%" + date + "%';", null);
        hour = new Number[cursor.getCount()];
        kw = new Number[cursor.getCount()];
        int i = 0;
        while (cursor.moveToNext()) {
            hour[i] = cursor.getInt(0);//hour
            kw[i] = cursor.getDouble(1);//kw
            i++;
        }
        return new Number[][]{hour, kw};
    }

    public Number[][] getMonthAverage() {
        Number[] timestamp, value;
        Cursor cursor = db.rawQuery("select strftime('%s',date(date))*1000, avg(kw) from consumption group by strftime('%m',date);", null);
        timestamp = new Number[cursor.getCount()];
        value = new Number[cursor.getCount()];

        int i = 0;
        while (cursor.moveToNext()) {
            timestamp[i] = cursor.getLong(0);
            value[i] = cursor.getDouble(1);
            //Log.d("dateas",timestamp[i]+","+value[i]);
            i++;
        }
        return new Number[][]{timestamp, value};
    }


    //// TODO: 16/02/2017 QUERY TO STRING ARRAY OF THIS (day): select strftime('%H',date),kw from consumption where date like '%2017-02-14%';
    //// TODO: 16/02/2017 QUERY TO STRING ARRAY OF THIS (month average): select date, avg(kw) from consumption group by strftime('%m',date);

    public String getDateTimeFormatted(int year, int month, int day, int hour) {
        String dayCorrected = (day >= 10) ? day + "" : "0" + day;
        String monthCorrected = (month >= 10) ? month + "" : "0" + month;
        String hourCorrected = (hour >= 10) ? hour + "" : "0" + hour;

        return year + "-" + monthCorrected + "-" + dayCorrected + " " + hourCorrected + ":00";
    }

    public void fillDatabase(SQLiteDatabase db, String startDate, int amount) {
        String insertQuery = "INSERT INTO " + TABLE_NAME + " (" + DATE_COLUMN + "," + KW_COLUMN + ") " +
                "values(";
        String[] values = generateValues(startDate, amount);
        db.beginTransaction();
        for (int i = 0; i < values.length; i++) {
            try {
                if (values[i] != null)
                    db.execSQL(insertQuery + "'" + values[i] + "'," + ((Math.random() * 0.8) + 0.1) + ");");
            } catch (Exception e) {
                db.endTransaction();
                e.printStackTrace();
            }
            Log.d("TRANSACTION", insertQuery + values[i] + "," + ((Math.random() * 0.8) + 0.1) + ");");
        }
        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
