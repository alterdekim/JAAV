package com.alterdekim.fridaapp.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.ParcelFileDescriptor;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.alterdekim.frida.FridaLib;
import com.alterdekim.frida.config.Config;
import com.alterdekim.fridaapp.App;
import com.alterdekim.fridaapp.util.Util;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

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
    private static final String VPN_ROUTE = "0.0.0.0"; // Intercept everything

    private ParcelFileDescriptor vpnInterface = null;

    private String logPath;

    private Disposable vpnProcess;

    private final FridaLib lib = new FridaLib();

    private int uid = -1;

    @Override
    public void onCreate() {
        Log.i(TAG, "Created");
        LocalBroadcastManager
                .getInstance(this)
                .registerReceiver(new ServiceEchoReceiver(), new IntentFilter("ping"));
    }

    private void setupVPN(com.alterdekim.fridaapp.room.Config _config) {
        try {
                Config config = new ObjectMapper(new YAMLFactory()).readValue(_config.getData_raw(), Config.class);
                File outputDir =  this.getCacheDir(); // context being the Activity pointer
                File outputFile = new File(outputDir, "fridalib.log");
                if( outputFile.exists() ) { outputFile.delete(); }
                outputFile.createNewFile();
                this.logPath = outputFile.getAbsolutePath();
//                Log.i(TAG, logPath);
//                new Thread(() -> {
//                    try {
//                        BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(outputFile)));
//                        String str = "";
//                        Log.i(TAG, "Reading fd has started");
//                        while (true) {
//                            if((str = br.readLine()) != null) {
//                                Log.i(TAG, str);
//                            }
//                        }
//                    } catch (Exception e) {
//                        Log.e(TAG, e.getMessage());
//                    }
//                }).start();

                Builder builder = new Builder();
                builder.setMtu(1400);
                builder.addAddress(config.getClient().getAddress(), 24);
                builder.addRoute(VPN_ROUTE, 0);
                //builder.addDnsServer("8.8.8.8");
                for( String packageName : _config.getPackages() ) {
                    if( _config.isAllowed() ) {
                        builder.addAllowedApplication(packageName);
                        continue;
                    }
                    builder.addDisallowedApplication(packageName);
                }
                if( !_config.isAllowed() ) builder.addDisallowedApplication("com.alterdekim.fridaapp");
                vpnInterface = builder.establish();
        } catch (Exception e) {
            Log.e(TAG, "error", e);
        }
    }

    @Override
    public void onDestroy() {
        App app = (App) getApplication();

        app.getDb()
            .configDao()
            .disableAll()
            .subscribe();

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

        com.alterdekim.fridaapp.room.Config config = (com.alterdekim.fridaapp.room.Config) intent.getExtras().getSerializable("config");

        int cfg_uid = config != null ? config.getUid() : -1;

        boolean state = intent.getExtras().getBoolean("vpnState");
        if(!state) {
            this.lib.stop();
            return START_STICKY;
        }
        if(cfg_uid != this.uid && this.uid != -1) {
            this.lib.stop();
            turnOff();
        }
        this.uid = cfg_uid;
        setupVPN(config);

        byte[] _cc = config != null ? config.getData_raw() : new byte[0];

        this.vpnProcess = Flowable.fromRunnable(new NativeBinaryConnection(vpnInterface.detachFd(), Util.bytesToHex(_cc), lib, logPath))
                .subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe();

        return START_STICKY;
    }

    private class ServiceEchoReceiver extends BroadcastReceiver {
        public void onReceive (Context context, Intent intent) {
            LocalBroadcastManager
                    .getInstance(FridaService.this)
                    .sendBroadcastSync(new Intent("pong"));
        }
    }
}
