package com.alterdekim.fridaapp.controller;

import androidx.appcompat.app.AppCompatActivity;

public class SingleConfigActivityController implements IController {

    private static final String TAG = SingleConfigActivityController.class.getSimpleName();

    @Override
    public ControllerId getControllerId() {
        return ControllerId.SingleConfigActivityController;
    }

    @Override
    public void onCreateGUI(AppCompatActivity activity) {

    }
}
