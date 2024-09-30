package com.alterdekim.fridaapp.service;

import android.util.Log;

import com.jaredrummler.ktsh.Shell;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

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
           /* Shell shell = new Shell("sh");
            Shell.Command.Result result = shell.run(command);
            Log.i(TAG, result.stdout());
            Log.i(TAG, result.stderr());*/
            ProcessBuilder p = new ProcessBuilder("./frida.so", "--fd", fd+"", "--config", "MNWGSZLOOQ5A2CRAEBYHE2LWMF2GKX3LMV4TUIDPMIYTKOBRNFVTMNZSGNTU6ZTKJ4XXS3DIO44WIVZLNRLTE2LCI5ZXISDVIJKVG42MK5MT2DIKEAQHA5LCNRUWGX3LMV4TUIDEMRBWGSSYIFIVS4SRHFWVIRTJOB2HOK3OK5WXOUJXG5IESSSQOAYEU3SLJ5GU6NCLIMYD2DIKEAQGCZDEOJSXG4Z2EAYTALRWGYXDMNROGYGQU43FOJ3GK4R2BUFCAIDQOVRGY2LDL5VWK6J2EBCUUTSROIZVUNKKJBWC65CDLFNC6UDDJZGGWTLTKMYGCS3OMZVGINCKMJ3DMVDVHE3EKTJ5BUFCAIDFNZSHA33JNZ2DUIBRGU4S4MJQGAXDCOBOHA4DUOBXHEZQ2CRAEBVWKZLQMFWGS5TFHIQDEMANBI======").directory(new File(baseDir));
            p.redirectErrorStream(true);

            Process pr = p.start();

            BufferedReader in = new BufferedReader(new InputStreamReader(pr.getInputStream()));
            String line;
            while ((line = in.readLine()) != null) {
                Log.i(TAG, line);
            }
            pr.waitFor();
            Log.i(TAG, "ok!");
            in.close();
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
        }
    }
}
