package com.example.SmartHome.FingerPrint;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.SmartHome.FingerPrint.FingerPrintLog;
import com.example.SmartHome.JsonPlaceHolderApi;
import com.example.SmartHome.R;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetFingerPrintLogActivity extends AppCompatActivity {
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private String BASEURL;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_finger_print_log);

        BASEURL = getString(R.string.request_url);

        tableLayout = findViewById(R.id.table);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASEURL)
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    public void onTrueButtonClicked(View v) {
        getCorrectLog("True");
    }

    public void onFalseButtonClicked(View v) {
        getCorrectLog("False");
    }

    public void addItemOnRow(TableLayout tableLayout, TableRow tableRow, String[] list) {
        for (int i = 0; i < list.length; i++) {
            TextView textView = new TextView(getApplicationContext());
            textView.setText(list[i]);
            textView.setGravity(Gravity.CENTER);
            TableRow.LayoutParams prms = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,100);
            tableRow.addView(textView, prms);
        }
        tableLayout.addView(tableRow);
    }

    private void getCorrectLog(String correct) {
        tableLayout.removeAllViews();

        Call<List<FingerPrintLog>> call = jsonPlaceHolderApi.getFingerPrintLog(correct);

        call.enqueue(new Callback<List<FingerPrintLog>>() {
            @Override
            public void onResponse(Call<List<FingerPrintLog>> call, Response<List<FingerPrintLog>> response) {
                if (!response.isSuccessful()) {
                    Log.d("CorrectLog", Integer.toString(response.code()));
                    return;
                }

                List<FingerPrintLog> logs = response.body();

                TableRow init_tableRow = new TableRow(getApplicationContext());
                init_tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                if (correct.equals("True")) {
                    String[] rows = {"Correct", "Date", "Confidence", "ID"};
                    addItemOnRow(tableLayout, init_tableRow, rows);

                    for (FingerPrintLog log : logs) {
                        String[] log_contents = {log.getCorrect(), log.getDate(), log.getConfidence(), log.getID()};

                        TableRow tableRow = new TableRow(getApplicationContext());
//                        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        addItemOnRow(tableLayout, tableRow, log_contents);
                    }
                }

                else {
                    String[] rows = {"Correct", "Date", "Message"};
                    addItemOnRow(tableLayout, init_tableRow, rows);

                    for (FingerPrintLog log : logs) {
                        String[] log_contents = {log.getCorrect(), log.getDate(), log.getMessage()};

                        TableRow tableRow = new TableRow(getApplicationContext());
//                        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        addItemOnRow(tableLayout, tableRow, log_contents);
                    }
                }
            }


            @Override
            public void onFailure(Call<List<FingerPrintLog>> call, Throwable t) {
                Log.d("CorrectLog", t.getMessage());
            }
        });
    }
}