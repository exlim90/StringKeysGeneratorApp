package com.example.vladimirangeleski.stringkeysgeneratorapp;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //Log.d("MainActivity", "key = "+TranslationKeys.app_name);
        String aa = getResources().getResourceEntryName(R.string.TEST_STRING_1);
        Log.d("AAA", aa);
    }
}
