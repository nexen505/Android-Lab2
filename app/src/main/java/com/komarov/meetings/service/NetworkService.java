package com.komarov.meetings.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.komarov.meetings.R;

public class NetworkService extends IntentService {
    private static final String ACTION_CHECK_NETWORK = "com.komarov.meetings.service.action.ACTION_CHECK_NETWORK";

    private static final String TAG = "NetworkService";

    public NetworkService() {
        super("NetworkService");
    }

    public static void startActionCheckNetwork(Context context) {
        Intent intent = new Intent(context, NetworkService.class);
        intent.setAction(ACTION_CHECK_NETWORK);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_CHECK_NETWORK.equals(action)) {
                handleActionCheckNetwork();
            }
        }
    }

    private void handleActionCheckNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        if (networkInfo == null) {
            Log.d(TAG, "Сеть недоступна");
            AlertDialog.Builder ad = new AlertDialog.Builder(this);
            ad.setTitle(R.string.network_warning_title);
            ad.setMessage(R.string.network_warning_message);
            ad.setCancelable(false);
            ad.setNegativeButton(R.string.button_cancel, (dialog, arg1) -> {
               /* Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);*/
            });
            ad.show();
        } else {
            boolean isNetworkAvailable = networkInfo.isConnectedOrConnecting();
            if (!isNetworkAvailable) {
                Log.d(TAG, "Сеть доступна");
            }
        }
    }

}
