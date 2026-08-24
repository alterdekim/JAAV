package com.alterdekim.fridaapp.controller;

import android.content.res.Resources;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.alterdekim.fridaapp.App;
import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.activity.SingleConfigActivity;
import com.alterdekim.fridaapp.adapter.AppListAdapter;
import com.alterdekim.fridaapp.adapter.AppPopUp;
import com.alterdekim.fridaapp.room.Config;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class SingleConfigActivityController implements IController {

    private static final String TAG = SingleConfigActivityController.class.getSimpleName();

    private Config config;
    private SingleConfigActivity activity;
    private List<AppPopUp> appsList = new ArrayList<>();
    private AppListAdapter adapter;

    @Override
    public ControllerId getControllerId() {
        return ControllerId.SingleConfigActivityController;
    }

    @Override
    public void onCreateGUI(AppCompatActivity activity) {
        this.activity = (SingleConfigActivity) activity;
        TextView config_name = this.activity.findViewById(R.id.interface_name);
        TextView public_key_text = this.activity.findViewById(R.id.public_key);
        TextView address_text = this.activity.findViewById(R.id.address);
        TextView endpoint = this.activity.findViewById(R.id.endpoint);

        config_name.setText(this.config.getTitle());

        try {
            com.alterdekim.frida.config.Config configData = config.getParsed();

            public_key_text.setText(configData.getServer().getPublic_key());
            address_text.setText(configData.getClient().getAddress());
            endpoint.setText(configData.getServer().getEndpoint());
        } catch (IOException e) {
            Toast.makeText(this.activity, R.string.config_open_error, Toast.LENGTH_LONG).show();
            this.activity.finish();
            return;
        }

        LinearLayout switch_allowed = this.activity.findViewById(R.id.switch_all);
        LinearLayout switch_disallowed = this.activity.findViewById(R.id.switch_dis);

        Resources resources = this.activity.getResources();

        switch_allowed.setOnClickListener(view -> {
            view.setBackground(resources.getDrawable(R.drawable.layout_swl));
            ( (TextView) activity.findViewById(R.id.btn_text_dis) ).setTextColor(resources.getColor(R.color.switch_deselected));
            ( (TextView) activity.findViewById(R.id.btn_text_all) ).setTextColor(resources.getColor(R.color.switch_selected));
            switch_disallowed.setBackground(null);
            config.setAllowed(true);
            updateConfig();
        });

        switch_disallowed.setOnClickListener(view -> {
            view.setBackground(resources.getDrawable(R.drawable.layout_swr));
            ( (TextView) activity.findViewById(R.id.btn_text_dis) ).setTextColor(resources.getColor(R.color.switch_selected));
            ( (TextView) activity.findViewById(R.id.btn_text_all) ).setTextColor(resources.getColor(R.color.switch_deselected));
            switch_allowed.setBackground(null);
            config.setAllowed(false);
            updateConfig();
        });

        this.appsList.clear();
        adapter = new AppListAdapter(this.appsList, (position, view) -> removeApp(position));
        this.activity.getAppsList().setAdapter(adapter);

        if(!config.isAllowed()) {
            switch_disallowed.performClick();
        }
    }

    private void syncList() {
        ArrayList<String> l = new ArrayList<>();
        for( AppPopUp a : this.appsList ) {
            l.add(a.getPackageName());
        }
        config.setPackages(l);
        updateConfig();
    }

    private void updateConfig() {
        App app = (App) this.activity.getApplication();
        app.getDb().userDao().insertAll(config)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new CompletableObserver() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        Log.e("cock", e.toString());
                    }
                });
    }

    private void removeApp(int position) {
        this.appsList.remove(position);
        this.syncList();
        adapter.notifyDataSetChanged();
    }

    public void addApp(AppPopUp app, boolean syncNeeded) {
        if(this.appsList.contains(app)) return;
        this.appsList.add(app);
        if( syncNeeded ) this.syncList();
        adapter.notifyDataSetChanged();
    }

    public void onConfigDataAppeared(Config config) {
        this.config = config;
    }
}
