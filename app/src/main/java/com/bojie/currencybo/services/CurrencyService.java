package com.bojie.currencybo.services;

import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcel;
import android.support.v4.os.ResultReceiver;
import android.text.TextUtils;

import com.bojie.currencybo.Constants;
import com.bojie.currencybo.helper.CurrencyParserHelper;
import com.bojie.currencybo.utils.LogUtils;
import com.bojie.currencybo.utils.WebServiceUtils;
import com.bojie.currencybo.value_objects.Currency;

import org.json.JSONObject;

/**
 * Created by bojiejiang on 1/20/16.
 */
public class CurrencyService extends IntentService {

    public static final String TAG = CurrencyService.class.getSimpleName();

    public CurrencyService(String name) {
        super(TAG);
    }

    public CurrencyService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        LogUtils.log(TAG, "Currency Service has started");
        Bundle intentBundle = intent.getBundleExtra(Constants.BUNDLE);
        final ResultReceiver receiver = intentBundle.getParcelable(Constants.RECEIVER);
        Parcel parcel = Parcel.obtain();
        receiver.writeToParcel(parcel, 0);
        parcel.setDataPosition(0);
        ResultReceiver receiverForSending =
               ResultReceiver.CREATOR.createFromParcel(parcel);
        parcel.recycle();

        String url = intentBundle.getString(Constants.URL);
        String currencyName = intentBundle.getString(Constants.CURRENCY_NAME);

        Bundle bundle = new Bundle();
        if (url != null && !TextUtils.isEmpty(url)) {
            receiverForSending.send(Constants.STATUS_RUNNING, Bundle.EMPTY);
            if (WebServiceUtils.hasInternetConnection(getApplicationContext())) {
                try {
                    JSONObject obj = WebServiceUtils.requestJSONObject(url);
                    if (obj != null) {
                        Currency currency = CurrencyParserHelper.parseCurrency(obj, currencyName);
                        bundle.putParcelable(Constants.RESULT, currency);
                        receiverForSending.send(Constants.STATUS_FINISHED, bundle);
                    }
                }
            }
        }
    }
}
