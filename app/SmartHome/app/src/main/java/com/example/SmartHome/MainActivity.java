package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        String BASEURL = getString(R.string.request_url);
    }

    public void saveButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), SavePeopleActivity.class);
        startActivity(intent);
    }

    public void getButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetPeoplesActivity.class);
        startActivity(intent);
    }

}