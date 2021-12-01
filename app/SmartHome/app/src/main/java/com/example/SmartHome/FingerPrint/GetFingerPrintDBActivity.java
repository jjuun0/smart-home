package com.example.SmartHome.FingerPrint;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.SmartHome.JsonPlaceHolderApi;
import com.example.SmartHome.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetFingerPrintDBActivity extends AppCompatActivity {
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private String BASEURL;

    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_finger_print_db);

        BASEURL = getString(R.string.request_url);

        tableLayout = findViewById(R.id.fingerprint_db_table);


        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        getFingerPrintDB();
    }

    public void getLogButtonClicked(View v) {
        Intent intent = new Intent(getApplicationContext(), GetFingerPrintLogActivity.class);
        startActivity(intent);
    }

    public void addItemOnRow(TableLayout tableLayout, TableRow tableRow, String[] list) {
        for (int i = 0; i < list.length; i++) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText(list[i]);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            TableRow.LayoutParams prms = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,100);
            tableRow.addView(textView, prms);
        }
        tableLayout.addView(tableRow);
    }

    private void getFingerPrintDB() {
        Call<List<FingerPrintDB>> call = jsonPlaceHolderApi.getFingerPrintDB();

        call.enqueue(new Callback<List<FingerPrintDB>>() {
            @Override
            public void onResponse(Call<List<FingerPrintDB>> call, Response<List<FingerPrintDB>> response) {
                if (!response.isSuccessful()) {
                    Log.d("Log", Integer.toString(response.code()));
                    return;
                }

                List<FingerPrintDB> db = response.body();

                for (FingerPrintDB item : db) {
                    TableRow tableRow = new TableRow(getApplicationContext());
//                    tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    String[] rows = {item.getID(), item.getName()};
                    addItemOnRow(tableLayout, tableRow, rows);
                }
            }

            @Override
            public void onFailure(Call<List<FingerPrintDB>> call, Throwable t) {
                Log.d("Log", t.getMessage());
            }
        });
    }


}