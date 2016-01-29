package com.bojie.currencybo.utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.bojie.currencybo.Constants;

/**
 * Created by bojiejiang on 1/28/16.
 */
public class SharedPreferencesUtils {

    public static String getCurrency(Context context, boolean isBaseCurrency) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE);

        return sharedPreferences.getString(
                isBaseCurrency ? Constants.BASE_CURRENCY: Constants.TARGET_CURRENCY,
                isBaseCurrency ? "USD" : "CNY");

    }

    public static void updateCurrency(Context context, String currency, boolean isBaseCurrency) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(isBaseCurrency ? Constants.BASE_CURRENCY : Constants.TARGET_CURRENCY, currency);
        editor.apply();
    }

    public static int getServiceRepetition(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE);
        return  sharedPreferences.getInt(Constants.SERVICE_REPETITION, 0);
    }

    public static void updateServiceRepetion(Context context, int serviceRepetition) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.SERVICE_REPETITION, serviceRepetition);
        editor.apply();
    }

    public static int getNumDownloads(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE);
        return  sharedPreferences.getInt(Constants.NUM_DOWNLOADS, 0);
    }

    public static void updateNumdownload(Context context, int numDownload) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(
                Constants.CURRENCY_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(Constants.NUM_DOWNLOADS, numDownload);
        editor.apply();
    }

}
