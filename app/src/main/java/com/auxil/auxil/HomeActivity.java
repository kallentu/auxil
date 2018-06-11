package com.auxil.auxil;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/** The base of Auxil */
public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }
}
