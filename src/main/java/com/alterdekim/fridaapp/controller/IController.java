package com.alterdekim.fridaapp.controller;

import androidx.appcompat.app.AppCompatActivity;

public interface IController {
    ControllerId getControllerId();
    void onCreateGUI(AppCompatActivity activity);
}
