package com.example.SmartHome;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.SmartHome.CCTV.StreamingActivity;
import com.example.SmartHome.CCTV.getCCTVActivity;
import com.example.SmartHome.Face.GetFaceDBActivity;
import com.example.SmartHome.FingerPrint.GetFingerPrintDBActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void getFaceButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), GetFaceDBActivity.class);
        startActivity(intent);
    }

    //    public void getVideoButtonClicked(View v){
//        Intent intent = new Intent(getApplicationContext(), GetVideoActivity.class);
//        startActivity(intent);
//    }
    public void getFingerPrintButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), GetFingerPrintDBActivity.class);
        startActivity(intent);
    }

    public void getVideoButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), getCCTVActivity.class);
        startActivity(intent);
    }

    public void getStreamingButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), StreamingActivity.class);
        startActivity(intent);
    }


    public void AdminButtonClicked(View v) {
        showPasswordDialog();
//        Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
//        startActivity(intent);
    }

    private void showPasswordDialog() {
        LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        LinearLayout passwordLayout = (LinearLayout) vi.inflate(R.layout.admin_password, null);
        final EditText pw = (EditText) passwordLayout.findViewById(R.id.pw);
        new AlertDialog.Builder(this).setTitle("Admin").setView(passwordLayout).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
//                Log.d("pw", pw.getText().toString());
                if (pw.getText().toString().equals(getString(R.string.admin_password))) {
                    Intent intent = new Intent(getApplicationContext(), AdminActivity.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(MainActivity.this, "비밀번호가 일치하지 않습니다", Toast.LENGTH_SHORT).show();
                }
            }
        }).show();
    }


}