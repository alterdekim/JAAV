package com.alterdekim.fridaapp;

import android.app.Application;

import androidx.room.Room;

import com.alterdekim.fridaapp.room.AppDatabase;

import lombok.Getter;

@Getter
public class App extends Application {
    private AppDatabase db;

    @Override
    public void onCreate() {
        super.onCreate();
        this.db = Room.databaseBuilder(this, AppDatabase.class, "def-db")
                .fallbackToDestructiveMigration()
                .build();
    }
}
