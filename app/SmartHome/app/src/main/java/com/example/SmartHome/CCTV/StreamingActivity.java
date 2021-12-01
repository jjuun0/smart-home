package com.example.SmartHome.CCTV;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;

import com.example.SmartHome.R;

import java.net.URL;

public class StreamingActivity extends AppCompatActivity {
    EditText address;
    WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streaming);

        address = findViewById(R.id.IPAddress);
        webView = findViewById(R.id.web);
    }

    //    public void IPAddButtonClicked(View v) {
////        Intent intent = new Intent(Intent.ACTION_VIEW);
////        intent.setData(Uri.parse("https://www.naver.com/"));
//////        intent.setData(Uri.parse(editText.toString()));
////        startActivity(intent);
//
//        webView.setWebViewClient(new WebViewClient());
////        webView.loadUrl("https://www.naver.com/");
//        webView.loadUrl(address.getText().toString());
//    }
    public void IPAddButtonClicked(View v) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(address.getText().toString()));
        startActivity(intent);
    }
}