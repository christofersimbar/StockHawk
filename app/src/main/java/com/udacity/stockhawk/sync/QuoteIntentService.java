package com.udacity.stockhawk.sync;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.widget.Toast;

import timber.log.Timber;


public class QuoteIntentService extends IntentService {
    Handler mHandler;

    public QuoteIntentService() {
        super(QuoteIntentService.class.getSimpleName());
        mHandler = new Handler();
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Timber.d("Intent handled");
        QuoteSyncJob.getQuotes(getApplicationContext(),mHandler);
    }
}
