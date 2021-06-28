package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.SmartHome.Face.GetFaceDBActivity;
import com.example.SmartHome.Face.GetFaceLogActivity;
import com.example.SmartHome.FingerPrint.GetFingerPrintDBActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetFaceDBActivity.class);
        startActivity(intent);
    }

    public void getVideoButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetVideoActivity.class);
        startActivity(intent);
    }

    public void getFingerPrintClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetFingerPrintDBActivity.class);
        startActivity(intent);
    }


}