package com.alterdekim.fridaapp.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

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
        try {
            Thread t = new Thread(new NativeBinaryConnection(vpnInterface.dup().detachFd(), getApplicationContext().getApplicationInfo().nativeLibraryDir));
            t.start();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }

    private void setupVPN() {
        try {
                Builder builder = new Builder();
                builder.setMtu(1400);
                builder.addAddress(VPN_ADDRESS, 24);
                builder.addRoute(VPN_ROUTE, 0);
                builder.addDnsServer("8.8.8.8");
                //builder.addAllowedApplication();
                builder.addDisallowedApplication("com.alterdekim.fridaapp");
                vpnInterface = builder.establish();
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_STICKY;
    }
}
