package com.alterdekim.fridaapp.service;

import android.content.Intent;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import com.alterdekim.frida.FridaLib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;

import io.reactivex.rxjava3.core.Flowable;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class FridaService extends VpnService {
    private static final String TAG = FridaService.class.getSimpleName();
    private static final String VPN_ADDRESS = "10.66.66.6"; // Only IPv4 support for now
    private static final String VPN_ROUTE = "0.0.0.0"; // Intercept everything

    private ParcelFileDescriptor vpnInterface = null;

    private String logPath;

    private Disposable vpnProcess;

    private final FridaLib lib = new FridaLib();

    @Override
    public void onCreate() {
        Log.i(TAG, "Created");
    }

    private void setupVPN() {
        try {
                File outputDir =  this.getCacheDir(); // context being the Activity pointer
                File outputFile = new File(outputDir, "fridalib.log");
                if( outputFile.exists() ) { outputFile.delete(); }
                outputFile.createNewFile();
                this.logPath = outputFile.getAbsolutePath();
                Log.i(TAG, logPath);
                /*new Thread(() -> {
                    try {
                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile)));
                        String str = "";
                        Log.i(TAG, "Reading fd has started");
                        while (true) {
                            if((str = br.readLine()) != null) {
                                Log.i(TAG, str);
                            }
                        }
                    } catch (Exception e) {
                        Log.e(TAG, e.getMessage());
                    }
                }).start();*/

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
        if( this.vpnProcess != null ) {
            Log.i(TAG, "DISPOSE");
            this.vpnProcess.dispose();
        }
    }

    private void turnOffVpn() {
        try {
            this.vpnInterface.close();
        } catch (IOException e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if( intent.getExtras() == null ) return START_STICKY;
        String hex = intent.getExtras().getString("vpn_hex");
        int uid = intent.getExtras().getInt("vpn_uid");
        boolean state = intent.getExtras().getBoolean("vpn_state");
        if(!state) {
            this.lib.stop();
            return START_STICKY;
        }
        setupVPN();
        // TODO: different configs
        this.vpnProcess = Flowable.fromRunnable(new NativeBinaryConnection(vpnInterface.detachFd(), hex, lib, logPath))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe();

        return START_STICKY;
    }
}
