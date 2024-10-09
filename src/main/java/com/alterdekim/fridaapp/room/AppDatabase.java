package com.alterdekim.fridaapp.room;

import androidx.room.Database;
import androidx.room.RoomDatabase;

@Database(entities = {Config.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ConfigDAO userDao();
}
