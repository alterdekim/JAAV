package com.alterdekim.frida;

import android.util.Log;

public class FridaLib {

    static {
        System.loadLibrary("frida");
    }

    public native int start(String config_hex, int tun_fd, boolean close_fd_on_drop, String temp_file);

    public native int stop();

    private static void traceFromNative(String text) {
        Log.i("FridaLib", text);
    }
}
