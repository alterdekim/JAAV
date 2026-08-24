package com.alterdekim.fridaapp.adapter;

import android.graphics.drawable.Drawable;

import androidx.annotation.Nullable;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class AppPopUp {
    private final String name;
    private final Drawable icon;
    private final String packageName;

    @Override
    public boolean equals(@Nullable Object obj) {
        if (!(obj instanceof AppPopUp)) return false;
        return this.packageName.equals(((AppPopUp) obj).packageName);
    }
}
