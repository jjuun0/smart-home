package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetPeoplesActivity.class);
        startActivity(intent);
    }

    public void getLogButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetLogActivity.class);
        startActivity(intent);
    }

    public void getVideoButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetVideoActivity.class);
        startActivity(intent);
    }

}