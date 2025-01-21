package com.alterdekim.fridaapp.controller;

import android.content.res.Resources;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.alterdekim.frida.config.Config;
import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.activity.SingleConfigActivity;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import java.io.IOException;

public class SingleConfigActivityController implements IController {

    private static final String TAG = SingleConfigActivityController.class.getSimpleName();

    private String config_title;
    private Config config_data;
    private SingleConfigActivity activity;
    //private List<String> appsList;
    //private boolean isAllowedApps;

    @Override
    public ControllerId getControllerId() {
        return ControllerId.SingleConfigActivityController;
    }

    @Override
    public void onCreateGUI(AppCompatActivity activity) {
        this.activity = (SingleConfigActivity) activity;
        //this.isAllowedApps = true;
        TextView config_name = this.activity.findViewById(R.id.interface_name);
        TextView public_key_text = this.activity.findViewById(R.id.public_key);
        TextView address_text = this.activity.findViewById(R.id.address);
        TextView endpoint = this.activity.findViewById(R.id.endpoint);

        config_name.setText(this.config_title);
        public_key_text.setText(this.config_data.getServer().getPublic_key());
        address_text.setText(this.config_data.getClient().getAddress());
        endpoint.setText(this.config_data.getServer().getEndpoint());

        LinearLayout switch_allowed = this.activity.findViewById(R.id.switch_all);
        LinearLayout switch_disallowed = this.activity.findViewById(R.id.switch_dis);

        Resources resources = this.activity.getResources();

        switch_allowed.setOnClickListener(view -> {
            view.setBackground(resources.getDrawable(R.drawable.layout_swl));
            ( (TextView) activity.findViewById(R.id.btn_text_dis) ).setTextColor(resources.getColor(R.color.switch_deselected));
            ( (TextView) activity.findViewById(R.id.btn_text_all) ).setTextColor(resources.getColor(R.color.switch_selected));
            switch_disallowed.setBackground(null);
            //this.isAllowedApps = true;
        });

        switch_disallowed.setOnClickListener(view -> {
            view.setBackground(resources.getDrawable(R.drawable.layout_swr));
            ( (TextView) activity.findViewById(R.id.btn_text_dis) ).setTextColor(resources.getColor(R.color.switch_selected));
            ( (TextView) activity.findViewById(R.id.btn_text_all) ).setTextColor(resources.getColor(R.color.switch_deselected));
            switch_allowed.setBackground(null);
            //this.isAllowedApps = false;
        });
    }

    public void onConfigDataAppeared(String title, byte[] data) {
        this.config_title = title;
        try {
            this.config_data = new ObjectMapper(new YAMLFactory()).setAnnotationIntrospector(new JacksonAnnotationIntrospector()).readValue(data, Config.class);
        } catch (IOException e) {
            Toast.makeText(this.activity, R.string.config_open_error, Toast.LENGTH_LONG).show();
            this.activity.finish();
        }
    }
}
