package com.alterdekim.fridaapp.service;

import android.util.Log;

import com.alterdekim.frida.FridaLib;

import java.util.Objects;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NativeBinaryConnection implements Runnable {
    private static final String TAG = NativeBinaryConnection.class.getSimpleName();

    private final int fd;
    private final String hex;
    private final FridaLib lib;
    private final String tempFile;

    @Override
    public void run() {
        try {
            Log.i(TAG, "FD: " + this.fd);
            Log.i(TAG, "Starting Frida client");
            int r = lib.start(this.hex, this.fd, false, this.tempFile);
            Log.i(TAG, "Exit code: " + r);
        } catch (Exception e) {
            Log.e(TAG, Objects.requireNonNull(e.getMessage()));
        }
    }
}
