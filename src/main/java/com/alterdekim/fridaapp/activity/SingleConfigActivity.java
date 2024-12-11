package com.alterdekim.fridaapp.activity;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.alterdekim.fridaapp.R;
import com.alterdekim.fridaapp.controller.ControllerId;
import com.alterdekim.fridaapp.controller.ControllerManager;
import com.alterdekim.fridaapp.controller.SingleConfigActivityController;

public class SingleConfigActivity extends AppCompatActivity {

    private static final String TAG = SingleConfigActivity.class.getSimpleName();

    private SingleConfigActivityController controller;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_single_config);

        ControllerManager.putController(new SingleConfigActivityController());
        this.controller = (SingleConfigActivityController) ControllerManager.getController(ControllerId.SingleConfigActivityController);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });


    }
}