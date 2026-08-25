package com.alterdekim.fridaapp.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alterdekim.fridaapp.App;
import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.adapter.AppListAdapter;
import com.alterdekim.fridaapp.adapter.AppPopUp;
import com.alterdekim.fridaapp.adapter.AppPopUpAdapter;
import com.alterdekim.fridaapp.room.Config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SingleConfigActivity extends AppCompatActivity {
    private RecyclerView appsList;
    private Config config;
    private AppListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_config);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        appsList = this.findViewById(R.id.apps_list);
        appsList.setLayoutManager(new LinearLayoutManager(this));

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        this.config = (Config) bundle.getSerializable("config");

        this.initializeGUI();

        final PackageManager pm = this.getPackageManager();
        for( String packageName : config.getPackages() ) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                Drawable appIcon = appInfo.loadIcon(pm);
                String appName = appInfo.loadLabel(pm).toString();
                this.addApp(new AppPopUp(appName, appIcon, packageName), false);
            } catch (PackageManager.NameNotFoundException ignored) {}
        }

        this.findViewById(R.id.add_app).setOnClickListener(view -> {
            List<PackageInfo> apps = pm.getInstalledPackages(PackageManager.GET_PERMISSIONS | PackageManager.GET_META_DATA);
            AlertDialog.Builder builder = new AlertDialog.Builder(SingleConfigActivity.this)
                    .setTitle(R.string.choose_app);

            final AppPopUpAdapter names = new AppPopUpAdapter(SingleConfigActivity.this, R.layout.app_popup_item);

            for( int i = 0; i < apps.size(); i++ ) {
                final PackageInfo packageInfo = apps.get(i);
                if( packageInfo.requestedPermissions == null || !Arrays.asList(packageInfo.requestedPermissions).contains(Manifest.permission.INTERNET) ) continue;
                String appName = pm.getApplicationLabel(packageInfo.applicationInfo).toString();
                String packageName = packageInfo.packageName;
                Drawable appIcon = null;
                try {
                    appIcon = pm.getApplicationIcon(packageName);
                } catch (PackageManager.NameNotFoundException ignored) {}
                names.add(new AppPopUp(appName, appIcon, packageName));
            }

            names.sort((a1, a2) -> a1.getName().compareTo(a2.getName()));

            names.notifyDataSetChanged();
            builder.setAdapter(names, (dialogInterface, i) -> {
                this.addApp(names.getItem(i), true);
            });
            builder.create().show();
        });
    }

    private void initializeGUI() {
        TextView config_name = this.findViewById(R.id.interface_name);
        TextView public_key_text = this.findViewById(R.id.public_key);
        TextView address_text = this.findViewById(R.id.address);
        TextView endpoint = this.findViewById(R.id.endpoint);

        config_name.setText(this.config.getTitle());

        try {
            com.alterdekim.frida.config.Config configData = config.getParsed();

            public_key_text.setText(configData.getServer().getPublic_key());
            address_text.setText(configData.getClient().getAddress());
            endpoint.setText(configData.getServer().getEndpoint());
        } catch (IOException e) {
            Toast.makeText(this, R.string.config_open_error, Toast.LENGTH_LONG).show();
            this.finish();
            return;
        }

        LinearLayout switch_allowed = this.findViewById(R.id.switch_all);
        LinearLayout switch_disallowed = this.findViewById(R.id.switch_dis);

        Resources resources = this.getResources();

        switch_allowed.setOnClickListener(view -> {
            view.setBackground(resources.getDrawable(R.drawable.layout_swl));
            ( (TextView) findViewById(R.id.btn_text_dis) ).setTextColor(resources.getColor(R.color.switch_deselected));
            ( (TextView) findViewById(R.id.btn_text_all) ).setTextColor(resources.getColor(R.color.switch_selected));
            switch_disallowed.setBackground(null);
            config.setAllowed(true);
            updateConfig();
        });

        switch_disallowed.setOnClickListener(view -> {
            view.setBackground(resources.getDrawable(R.drawable.layout_swr));
            ( (TextView) findViewById(R.id.btn_text_dis) ).setTextColor(resources.getColor(R.color.switch_selected));
            ( (TextView) findViewById(R.id.btn_text_all) ).setTextColor(resources.getColor(R.color.switch_deselected));
            switch_allowed.setBackground(null);
            config.setAllowed(false);
            updateConfig();
        });

        adapter = new AppListAdapter(new ArrayList<>(), (position, view) -> removeApp(position));
        this.appsList.setAdapter(adapter);

        if(!config.isAllowed()) switch_disallowed.performClick();
    }

    private void syncList() {
        ArrayList<String> l = new ArrayList<>();
        for( AppPopUp a : this.adapter.getAppList() ) {
            l.add(a.getPackageName());
        }
        config.setPackages(l);
        updateConfig();
    }

    private void removeApp(int position) {
        this.adapter.getAppList().remove(position);
        this.syncList();
        adapter.notifyDataSetChanged();
    }

    public void addApp(AppPopUp app, boolean syncNeeded) {
        if(this.adapter.getAppList().contains(app)) return;
        this.adapter.getAppList().add(app);
        if( syncNeeded ) this.syncList();
        adapter.notifyDataSetChanged();
    }

    private void updateConfig() {
        App app = (App) this.getApplication();

        app.getDb()
                .configDao()
                .insertAll(config)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(t -> Toast.makeText(this, R.string.config_open_error, Toast.LENGTH_LONG).show())
                .subscribe();
    }
}