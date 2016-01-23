package com.bojie.currencybo.database;

import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bojie.currencybo.Constants;
import com.bojie.currencybo.utils.LogUtils;

/**
 * Created by bojiejiang on 1/23/16.
 */
public class CurrencyDatabaseAdapter extends SQLiteOpenHelper {

    private static final String TAG = CurrencyDatabaseAdapter.class.getSimpleName();

    public static final int DATABASE_VERSION = 1;

    public static final String CURRENCY_TABLE_CREATE = "create table " +
            Constants.CURRENCY_TABLE + " (" +
            Constants.KEY_ID + " integer primary key autoincrement, " +
            Constants.KEY_BASE + " text not null, " +
            Constants.KEY_NAME + " text not null, " +
            Constants.KEY_RATE + " real, " +
            Constants.KEY_DATE + " date);";

    public CurrencyDatabaseAdapter(Context context) {
        super(context, Constants.DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            db.execSQL(CURRENCY_TABLE_CREATE);
            LogUtils.log(TAG, "Currency table created");
        } catch (SQLException e) {
            e.printStackTrace();
            LogUtils.log(TAG, "Currency Table creation error");
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        clearCurrencyTable(db);
        onCreate(db);
    }

    private void clearCurrencyTable(SQLiteDatabase db) {
        db.execSQL("DROP TABLE IF EXISTS " + Constants.CURRENCY_TABLE);
    }
}
