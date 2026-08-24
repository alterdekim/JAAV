package com.alterdekim.fridaapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.alterdekim.fridaapp.R;

import java.util.List;

public class AppPopUpAdapter extends ArrayAdapter<AppPopUp> {

    private final int resource;

    public AppPopUpAdapter(@NonNull Context context, int resource) {
        super(context, resource);
        this.resource = resource;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        if (v == null) v = LayoutInflater.from(this.getContext()).inflate(resource, null);

        AppPopUp p = this.getItem(position);

        if(p == null) return v;

        TextView appName = (TextView) v.findViewById(R.id.app_name);
        ImageView appIcon = (ImageView) v.findViewById(R.id.app_icon);

        appName.setText(p.getName());
        appIcon.setImageDrawable(p.getIcon());

        return v;
    }
}
