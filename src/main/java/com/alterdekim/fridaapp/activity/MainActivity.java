package com.alterdekim.fridaapp.activity;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.VpnService;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alterdekim.fridaapp.App;
import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.adapter.TunnelListAdapter;
import com.alterdekim.fridaapp.room.Config;
import com.alterdekim.fridaapp.util.Util;
import com.alterdekim.fridaapp.service.FridaService;

import java.io.IOException;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener {

    ActivityResultLauncher<Intent> pickerActivityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != Activity.RESULT_OK) return;
                Intent data = result.getData();
                try {
                    String raw_data = Util.readTextFromUri(this, data.getData());
                    String name = Util.getFilenameFromUri(this, data.getData());
                    insertNewConfig(name, raw_data.getBytes());
                } catch (IOException | NullPointerException e) {
                    Toast.makeText(this, R.string.config_adding_error, Toast.LENGTH_LONG).show();
                }
            });
    private final ActivityResultLauncher<Intent> launcher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() != RESULT_OK) {
                    this.disableAllTunnels();
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.vpn_access_denied)
                            .show();
                    return;
                }
                startVpnService();
            }
    );

    private boolean serviceRunning = false;

    private BroadcastReceiver pong = new BroadcastReceiver() {
        public void onReceive (Context context, Intent intent) {
            serviceRunning = true;
        }
    };

    private TunnelListAdapter adapter;
    private Disposable configFetchJob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        App app = (App) this.getApplication();

        // Sadly, that's the only reliable way to check whether service is running...
        serviceRunning = false;
        LocalBroadcastManager.getInstance(this).registerReceiver(pong, new IntentFilter("pong"));
        LocalBroadcastManager.getInstance(this).sendBroadcastSync(new Intent("ping"));
        if(!serviceRunning) disableAllTunnels();

        RecyclerView configList = findViewById(R.id.config_list);
        configList.setLayoutManager(new LinearLayoutManager(this));

        this.adapter = new TunnelListAdapter(
                (position, view) -> {
                    Intent intent = new Intent(this, SingleConfigActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putSerializable("config", this.adapter.getConfigList().get(position));
                    intent.putExtras(bundle);
                    this.startActivity(intent);
                },
                (position, view) -> {
                    new AlertDialog.Builder(this)
                            .setMessage(R.string.confirm_config_removal)
                            .setPositiveButton(android.R.string.yes, (dialog, btn) -> {
                                Config c1 = adapter.getConfigList().get(position);
                                app.getDb()
                                        .userDao()
                                        .delete(c1)
                                        .subscribeOn(Schedulers.io())
                                        .doOnComplete(() -> {
                                            if( c1.isEnabled() ) this.startVpnService(null, false);
                                        })
                                        .doOnError(t -> Toast.makeText(this, R.string.config_open_error, Toast.LENGTH_LONG).show())
                                        .subscribe();
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                },
                (position, isEnabled) -> {
                    if(isEnabled) {
                        app.getDb()
                                .userDao()
                                .enableSingle(adapter.getConfigList().get(position).getUid())
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .doOnComplete(this::startVpn)
                                .subscribe();
                        return;
                    }
                    this.startVpnService(null, false);
                    this.disableAllTunnels();
                }
        );

        configList.setAdapter(this.adapter);

        if( this.configFetchJob != null ) this.configFetchJob.dispose();

        this.configFetchJob = app.getDb()
                .userDao()
                .getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(tunnels -> {
                    this.adapter.getConfigList().clear();
                    this.adapter.getConfigList().addAll(tunnels);
                    this.adapter.notifyDataSetChanged();
                },
            t -> {});

        findViewById(R.id.addConfig).setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(this, v);
            popup.setOnMenuItemClickListener(MainActivity.this);
            popup.getMenuInflater().inflate(R.menu.mm, popup.getMenu());
            popup.show();
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if( item.getItemId() != R.id.import_from_file ) return false;
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/*");
        pickerActivityResultLauncher.launch(intent);
        return true;
    }

    public void insertNewConfig(String name, byte[] config) {
        Config nConfig = new Config(name, config);
        try {
            // such a dumb, chunky way to check config structure validity but whatever.
            nConfig.getParsed();
        } catch (IOException e) {
            Toast.makeText(this, R.string.config_adding_error, Toast.LENGTH_LONG).show();
            return;
        }
        App app = (App) this.getApplication();
        app.getDb()
                .userDao()
                .insertAll(nConfig)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> Toast.makeText(this, R.string.config_adding_error, Toast.LENGTH_LONG).show())
                .subscribe();
    }

    private void disableAllTunnels() {
        App app = (App) this.getApplication();
        app.getDb()
                .userDao()
                .disableAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe();
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
        App app = (App) getApplication();

        app.getDb()
            .userDao()
            .getEnabled()
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnSuccess(config -> this.startVpnService(config, true))
            .doOnError(throwable -> this.startVpnService(null, false))
            .subscribe();
    }

    private void startVpnService(Config config, boolean state) {
        Intent intent = new Intent(this, FridaService.class);
        Bundle bundle = new Bundle();
        bundle.putSerializable("config", config);
        bundle.putBoolean("vpnState", state);
        intent.putExtras(bundle);
        this.startService(intent);
    }
}