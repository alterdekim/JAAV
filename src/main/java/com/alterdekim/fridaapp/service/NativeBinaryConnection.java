package com.alterdekim.fridaapp.service;

import android.util.Log;

import com.alterdekim.frida.FridaLib;

public class NativeBinaryConnection implements Runnable {
    private static final String TAG = NativeBinaryConnection.class.getSimpleName();

    private int fd = 0;
    private String baseDir;

    public NativeBinaryConnection(int fd, String baseDir) {
        this.fd = fd;
        this.baseDir = baseDir;
    }

    @Override
    public void run() {
        try {
            Log.i(TAG, "FD: " + this.fd);
            FridaLib lib = new FridaLib();
            Log.i(TAG, "Starting Frida client");
            int r = lib.start("<data>", this.fd, false);
            Log.i(TAG, "Exit code: " + r);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
