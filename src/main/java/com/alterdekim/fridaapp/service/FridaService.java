package com.alterdekim.fridaapp.service;

import android.app.PendingIntent;
import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import java.util.Objects;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;
import io.reactivex.rxjava3.subjects.BehaviorSubject;

public class FridaService extends VpnService {
    private static final String TAG = FridaService.class.getSimpleName();
    private static final String VPN_ADDRESS = "10.66.66.6"; // Only IPv4 support for now
    private static final String VPN_ROUTE = "0.0.0.0"; // Intercept everything

    private ParcelFileDescriptor vpnInterface = null;

    private Disposable vpnProcess;

    @Override
    public void onCreate() {
        Log.i(TAG, "Created");
        setupVPN();
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
    public void onDestroy() {
        turnOff();
    }

    private void turnOff() {
        if( this.vpnProcess != null && !this.vpnProcess.isDisposed() ) this.vpnProcess.dispose();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent.getExtras() == null ) return START_STICKY;
        String hex = intent.getExtras().getString("vpn_hex");
        int uid = intent.getExtras().getInt("vpn_uid");
        boolean state = intent.getExtras().getBoolean("vpn_state");
        turnOff();
        if(!state) return START_STICKY;
        // TODO: different configs
        /*this.vpnProcess = Flowable.fromRunnable(new NativeBinaryConnection(vpnInterface.detachFd(), hex))
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe();*/
        try {
            Thread t = new Thread(new NativeBinaryConnection(vpnInterface.dup().detachFd(), hex));
            t.start();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
        return START_STICKY;
    }
}
