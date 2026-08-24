package com.alterdekim.fridaapp.room;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@Entity
@Getter
@Setter
public class Config implements Serializable {
    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "title")
    private String title;

    @ColumnInfo(name = "data_raw")
    private byte[] data_raw;

    @ColumnInfo(name = "is_allowed")
    private boolean isAllowed;

    @ColumnInfo(name = "packages_list")
    private ArrayList<String> packages;

    public Config(String title, byte[] data_raw) {
        this.title = title;
        this.data_raw = data_raw;
        this.isAllowed = true;
        this.packages = new ArrayList<>();
    }

    public com.alterdekim.frida.config.Config getParsed() throws IOException {
        return new ObjectMapper(new YAMLFactory()).setAnnotationIntrospector(new JacksonAnnotationIntrospector()).readValue(this.data_raw, com.alterdekim.frida.config.Config.class);
    }
}
