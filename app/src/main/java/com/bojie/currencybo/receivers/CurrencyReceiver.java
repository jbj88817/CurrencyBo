package com.bojie.currencybo.receivers;

import android.os.Bundle;
import android.os.Handler;
import android.support.v4.os.ResultReceiver;

/**
 * Created by bojiejiang on 1/20/16.
 */
public class CurrencyReceiver extends ResultReceiver {

    private Receiver mReceiver;

    public  CurrencyReceiver(Handler handler) {
        super(handler);
    }

    public void setReceiver(Receiver receiver) {
        mReceiver = receiver;
    }

    public interface Receiver {
        void onReceiverResult(int resultCode, Bundle resultData);
    }

    @Override
    protected void onReceiveResult(int resultCode, Bundle resultData) {
        if (mReceiver != null) {
            mReceiver.onReceiverResult(resultCode, resultData);
        }
    }
}
