package com.alterdekim.fridaapp.activity;

import android.app.Activity;
import android.content.Intent;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.controller.ControllerId;
import com.alterdekim.fridaapp.controller.ControllerManager;
import com.alterdekim.fridaapp.controller.MainActivityController;
import com.alterdekim.fridaapp.util.Util;
import com.alterdekim.fridaapp.service.FridaService;

import java.io.IOException;

import lombok.Getter;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();

    private MainActivityController controller;

    @Getter
    private LinearLayout cfg_list;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) return;
                Intent data = result.getData();
                try {
                    String raw_data = Util.readTextFromUri(this, data.getData());
                    String name = Util.getFilenameFromUri(this, data.getData());
                    byte[] config = raw_data.getBytes();
                    this.controller.insertNewConfig(name, config);
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            });

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) return;
                startVpnService();
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        ControllerManager.putController(new MainActivityController());
        this.controller = (MainActivityController) ControllerManager.getController(ControllerId.MainActivityController);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        this.cfg_list = (LinearLayout) findViewById(R.id.config_list);

        this.controller.onCreateGUI(this);

        findViewById(R.id.addConfig).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(MainActivity.this);
            popup.getMenuInflater().inflate(R.menu.mm, popup.getMenu());
            popup.show();
        });

        startVpn();
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if( item.getItemId() == R.id.new_config ) {
            newConfig();
            return true;
        } else if( item.getItemId() == R.id.import_from_file ) {
            importConfigFromFile();
            return true;
        }
        return false;
    }

    private void newConfig() {
        Toast.makeText(this, "newConfig", Toast.LENGTH_LONG).show();
    }

    private void importConfigFromFile() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        someActivityResultLauncher.launch(intent);
    }

    private void startVpn() {
        Intent intent = VpnService.prepare(MainActivity.this);
        if (intent != null) {
            launcher.launch(intent);
            return;
        }
        startVpnService();
    }

    private void startVpnService() {
        startService(new Intent(this, FridaService.class));
    }
}