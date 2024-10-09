package com.alterdekim.fridaapp;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.net.VpnService;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.room.Room;

import com.alterdekim.fridaapp.room.AppDatabase;
import com.alterdekim.fridaapp.room.Config;
import com.alterdekim.fridaapp.service.FridaService;

import org.apache.commons.codec.binary.Base32;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {
    private static final String TAG = MainActivity.class.getSimpleName();
    private AppDatabase db;

    ActivityResultLauncher<Intent> someActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) return;
                Intent data = result.getData();
                try {
                    String raw_data = Util.readTextFromUri(this, data.getData());
                    String name = Util.getFilenameFromUri(this, data.getData());
                    String b32 = Base32.builder().get().encodeToString(raw_data.getBytes(StandardCharsets.UTF_8));
                    db.userDao().insertAll(new Config(name, b32));
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            });

    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) return;
                startService(new Intent(this, FridaService.class));
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        this.db = Room.databaseBuilder(getApplicationContext(), AppDatabase.class, "def-db").build();
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        findViewById(R.id.addConfig).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(MainActivity.this);
            popup.getMenuInflater().inflate(R.menu.mm, popup.getMenu());
            popup.show();
        });

        LayoutInflater inflater = getLayoutInflater();
        for( Config config : db.userDao().getAll() ) {

            View myLayout = inflater.inflate(R.layout.single_config, mainLayout, false);
        }
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
        startService(new Intent(this, FridaService.class));
    }
}