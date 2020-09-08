package com.example.studybuddy;

import android.app.Application;

import com.firebase.client.Firebase;

public class StudyBuddy extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }
}
