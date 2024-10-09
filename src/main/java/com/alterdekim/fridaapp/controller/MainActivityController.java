package com.alterdekim.fridaapp.controller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.alterdekim.fridaapp.room.AppDatabase;

public class MainActivityController implements IController {

    private AppDatabase db;

    @Override
    public ControllerId getControllerId() {
        return ControllerId.MainActivityController;
    }

    @Override
    public void onCreateGUI(AppCompatActivity activity) {
        this.db = Room.databaseBuilder(activity.getApplicationContext(), AppDatabase.class, "def-db").build();
    }
}
