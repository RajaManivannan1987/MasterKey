package com.imaginetventures.masterkey.activityClasses;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.imaginetventures.masterkey.R;
import com.imaginetventures.masterkey.utils.SharePreferrence.Session;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if(!new Session(SplashActivity.this).isLogin()) {
                    startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                }else{
                    startActivity(new Intent(SplashActivity.this, DashboardActivity.class));
                }
                finish();
            }
        }, 3000);
    }
}
