package com.bojie.currencybo;

import android.content.Intent;
import android.database.SQLException;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.bojie.currencybo.database.CurrencyDatabaseAdapter;
import com.bojie.currencybo.database.CurrencyTableHelper;
import com.bojie.currencybo.receivers.CurrencyReceiver;
import com.bojie.currencybo.services.CurrencyService;
import com.bojie.currencybo.utils.AlarmUtils;
import com.bojie.currencybo.utils.LogUtils;
import com.bojie.currencybo.utils.NotificationUtils;
import com.bojie.currencybo.utils.SharedPreferencesUtils;
import com.bojie.currencybo.value_objects.Currency;

public class MainActivity extends AppCompatActivity implements CurrencyReceiver.Receiver{

    private String mBaseCurrency = Constants.CURRENCY_CODES[30];
    private String mTargetCurrency = Constants.CURRENCY_CODES[5];
    private CurrencyTableHelper mCurrencyTableHelper;

    public static final String TAG = MainActivity.class.getSimpleName();
    private int mServiceRepetition = AlarmUtils.REPEAT.REPEAT_EVERY_MINUTE.ordinal();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resetDownloads();
        initDB();
        retrieveCurrencyExchangeRate();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public void onReceiverResult(int resultCode, final Bundle resultData) {
        switch (resultCode) {
            case Constants.STATUS_RUNNING:
                LogUtils.log(TAG, "Currency Service Running!");
                break;
            case Constants.STATUS_FINISHED:
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Currency currencyParcel = resultData.getParcelable(Constants.RESULT);
                        if (currencyParcel != null) {
                            String message = "Currency: " + currencyParcel.getBase() + " - " +
                                    currencyParcel.getName() + ": " + currencyParcel.getRate();
                            LogUtils.log(TAG, message);
                            long id = mCurrencyTableHelper.insertCurrency(currencyParcel);
                            Currency currency = currencyParcel;
                            try {
                                currency = mCurrencyTableHelper.getCurrency(id);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                LogUtils.log(TAG, "Currency retrieval has failed");
                            }
                            if (currency != null) {
                                String dbMessage = "Currency (DB): " + currency.getBase() + " - " +
                                        currency.getName() + ": " + currency.getRate();
                                LogUtils.log(TAG, dbMessage);
                                NotificationUtils.showNotificationMessage(getApplicationContext(),
                                        "Currency Exchange Rate", dbMessage);
                            }

                            if (NotificationUtils.isAppInBackground(MainActivity.this)) {
                                int numDownloads = SharedPreferencesUtils.getNumDownloads(
                                        getApplicationContext());
                                SharedPreferencesUtils.updateNumDownload(
                                        getApplicationContext(), ++numDownloads);
                                if (numDownloads == Constants.MAX_DOWNLOADS) {
                                    LogUtils.log(TAG, "Max downloads for the background processing has been reached.");
                                    mServiceRepetition = AlarmUtils.REPEAT.REPEAT_EVERY_DAY.ordinal();
                                    retrieveCurrencyExchangeRate();
                                }
                            }
                        }
                    }
                });
                break;

            case Constants.STATUS_ERROR:
                String error = resultData.getString(Intent.EXTRA_TEXT);
                LogUtils.log(TAG, error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
        }
    }

    private void initDB() {
        CurrencyDatabaseAdapter currencyDatabaseAdapter = new CurrencyDatabaseAdapter(this);
        mCurrencyTableHelper = new CurrencyTableHelper(currencyDatabaseAdapter);
    }

    private void retrieveCurrencyExchangeRate() {
        CurrencyReceiver receiver = new CurrencyReceiver(new Handler());
        receiver.setReceiver(this);
        Intent intent = new Intent(Intent.ACTION_SYNC, null,
                getApplicationContext(), CurrencyService.class);
        intent.setExtrasClassLoader(CurrencyService.class.getClassLoader());

        Bundle bundle = new Bundle();
        String url = Constants.CURRENCY_URL + mBaseCurrency;
        bundle.putString(Constants.URL, url);
        bundle.putParcelable(Constants.RECEIVER, receiver);
        bundle.putInt(Constants.REQUEST_ID, Constants.REQUEST_ID_NUM);
        bundle.putString(Constants.CURRENCY_NAME, mTargetCurrency);
        bundle.putString(Constants.CURRENCY_BASE, mBaseCurrency);
        intent.putExtra(Constants.BUNDLE, bundle);
        //startService(intent);
        AlarmUtils.startService(this, intent,
                AlarmUtils.REPEAT.values()[mServiceRepetition]);
    }

    private void resetDownloads() {
        SharedPreferencesUtils.updateNumDownload(this, 0);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
