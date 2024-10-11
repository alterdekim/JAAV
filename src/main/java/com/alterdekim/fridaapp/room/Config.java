package com.alterdekim.fridaapp.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@RequiredArgsConstructor
@Getter
@Setter
public class Config {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private final String title;

    @ColumnInfo(name = "data_hex")
    private final String data_hex;
}
