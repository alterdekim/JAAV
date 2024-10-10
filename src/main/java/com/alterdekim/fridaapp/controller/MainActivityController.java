package com.alterdekim.fridaapp.controller;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.activity.MainActivity;
import com.alterdekim.fridaapp.room.AppDatabase;
import com.alterdekim.fridaapp.room.Config;
import com.google.android.material.switchmaterial.SwitchMaterial;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.functions.Action;
import io.reactivex.rxjava3.functions.Consumer;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivityController implements IController {

    private static final String TAG = MainActivityController.class.getSimpleName();

    private AppDatabase db;

    private MainActivity mainActivity;

    @Override
    public ControllerId getControllerId() {
        return ControllerId.MainActivityController;
    }

    @Override
    public void onCreateGUI(AppCompatActivity activity) {
        this.db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "def-db").build();
        this.mainActivity = (MainActivity) activity;
        this.initConfigListGUI();
    }

    private void initConfigListGUI() {
        LayoutInflater inflater = this.mainActivity.getLayoutInflater();
        this.mainActivity.getCfg_list().removeAllViews();
        this.db.userDao().getAll()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(config -> {
                    View cfg_instance = inflater.inflate(R.layout.single_config, this.mainActivity.getCfg_list(), false);
                    this.mainActivity.getCfg_list().addView(cfg_instance);
                    TextView view_name = (TextView) cfg_instance.findViewById(R.id.config_name);
                    SwitchMaterial view_switch = (SwitchMaterial) cfg_instance.findViewById(R.id.config_switch);
                    view_switch.setUseMaterialThemeColors(true);
                    view_switch.setOnCheckedChangeListener((compoundButton, b) -> Log.i(TAG, "onCheckedChanged " + b));
                    view_name.setText(config.title);
                })
                .doAfterNext(config -> inflater.inflate(R.layout.single_divider, this.mainActivity.getCfg_list(), false))
                .subscribe();
    }

    public void insertNewConfig(String name, String b32) {
        db.userDao().insertAll(new Config(name, b32))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(throwable -> Toast.makeText(MainActivityController.this.mainActivity, R.string.config_adding_error, Toast.LENGTH_LONG).show())
                .doOnComplete(this::initConfigListGUI)
                .subscribe();
    }
}
