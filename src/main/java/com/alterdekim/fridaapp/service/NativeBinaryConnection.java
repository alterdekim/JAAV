package com.alterdekim.fridaapp.service;

import android.util.Log;

import com.alterdekim.frida.FridaLib;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NativeBinaryConnection implements Runnable {
    private static final String TAG = NativeBinaryConnection.class.getSimpleName();

    private final int fd;
    private final String hex;

    @Override
    public void run() {
        try {
            Log.i(TAG, "FD: " + this.fd);
            FridaLib lib = new FridaLib();
            Log.i(TAG, "Starting Frida client");
            Log.i(TAG, "Hex: " + this.hex);
            int r = lib.start(this.hex.toLowerCase(), this.fd, false);
            Log.i(TAG, "Exit code: " + r);
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
