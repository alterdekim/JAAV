package com.alterdekim.fridaapp.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Config {
    @PrimaryKey
    public int uid;

    @ColumnInfo(name = "title")
    public String title;

    @ColumnInfo(name = "data_b32")
    public String data_b32;

    public Config(String title, String data_b32) {
        this.title = title;
        this.data_b32 = data_b32;
    }
}
