package com.techsavanna.shiloahmsk.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import androidx.appcompat.app.AppCompatActivity;

import com.techsavanna.shiloahmsk.R;

public class Splash extends AppCompatActivity {

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_splash);
        new Handler().postDelayed(() -> {
            Splash.this.startActivity(new Intent(Splash.this, Login.class));
            Splash.this.finish();
        }, 2500);
    }
}
