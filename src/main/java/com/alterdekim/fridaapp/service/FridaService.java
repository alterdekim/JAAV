package com.alterdekim.fridaapp.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.alterdekim.fridaapp.R;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class FridaService extends VpnService {
    private static final String TAG = FridaService.class.getSimpleName();
    private static final String VPN_ADDRESS = "10.66.66.6"; // Only IPv4 support for now
    private static final String VPN_ROUTE = "0.0.0.0"; // Intercept everything

    private ParcelFileDescriptor vpnInterface = null;
    private PendingIntent pendingIntent;


    @Override
    public void onCreate() {
        setupVPN();
        Log.i(TAG, "Started");
        // .detachFd()
        try {
            Thread t = new Thread(new NativeBinaryConnection(vpnInterface.dup().getFd(), getApplicationContext().getApplicationInfo().nativeLibraryDir));
            t.start();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                OkHttpClient client = new OkHttpClient();


                Request request = new Request.Builder()
                        .url("https://google.com")
                        .build();
                try {
                    try (Response response = client.newCall(request).execute()) {
                        Log.i(TAG, "Response code: " + response.code());
                        if (response.body() != null) {
                            Log.i(TAG, "Response body: " + response.body().string());
                        } else {
                            Log.i(TAG, "Response body: null");
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, Objects.requireNonNull(e.getMessage()));
                }
            }
        }).start();
    }

    private void setupVPN() {
        try {
            //if (vpnInterface == null) {
                Builder builder = new Builder();
                builder.addAddress(VPN_ADDRESS, 32);
                builder.addRoute(VPN_ROUTE, 0);
                //builder.addDnsServer("1.1.1.1");
                //builder.setMtu(1400);
                //builder.addAllowedApplication();
                //builder.addDisallowedApplication();

                vpnInterface = builder.setSession(getString(R.string.app_name)).setConfigureIntent(pendingIntent).establish();
           // }
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
