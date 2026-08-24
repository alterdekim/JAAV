package com.alterdekim.fridaapp.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MotionEvent;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.adapter.AppPopUp;
import com.alterdekim.fridaapp.adapter.AppPopUpAdapter;
import com.alterdekim.fridaapp.controller.ControllerId;
import com.alterdekim.fridaapp.controller.ControllerManager;
import com.alterdekim.fridaapp.controller.SingleConfigActivityController;
import com.alterdekim.fridaapp.room.Config;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import lombok.Getter;

public class SingleConfigActivity extends AppCompatActivity {

    private static final String TAG = SingleConfigActivity.class.getSimpleName();

    @Getter
    private RecyclerView appsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_config);

        appsList = this.findViewById(R.id.apps_list);
        appsList.setLayoutManager(new LinearLayoutManager(this));

        ControllerManager.putController(new SingleConfigActivityController());
        SingleConfigActivityController controller = (SingleConfigActivityController) ControllerManager.getController(ControllerId.SingleConfigActivityController);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        Config config = (Config) bundle.getSerializable("config");

        controller.onConfigDataAppeared(config);
        controller.onCreateGUI(this);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        final PackageManager pm = this.getPackageManager();
        for( String packageName : config.getPackages() ) {
            try {
                ApplicationInfo appInfo = pm.getApplicationInfo(packageName, 0);
                Drawable appIcon = appInfo.loadIcon(pm);
                String appName = appInfo.loadLabel(pm).toString();
                controller.addApp(new AppPopUp(appName, appIcon, packageName), false);
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
                controller.addApp(names.getItem(i), true);
            });
            builder.create().show();
        });
    }
}