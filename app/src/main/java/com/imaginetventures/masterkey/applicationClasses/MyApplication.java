package com.imaginetventures.masterkey.applicationClasses;

import android.app.Application;

import com.imaginetventures.masterkey.utils.font.FontsOverride;

/**
 * Created by Sanjay on 3/24/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FontsOverride.setDefaultFont(this, "DEFAULT", "fonts/helvetica_neue.ttf");
    }

}
