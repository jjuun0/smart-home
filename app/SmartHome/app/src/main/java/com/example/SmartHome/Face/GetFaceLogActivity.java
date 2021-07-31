package com.example.SmartHome.Face;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.example.SmartHome.JsonPlaceHolderApi;
import com.example.SmartHome.R;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetFaceLogActivity extends AppCompatActivity {
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private String BASEURL;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_face_log);

        BASEURL = getString(R.string.request_url);

        tableLayout = findViewById(R.id.table);

        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    }

    public void onTrueButtonClicked(View v){
        getCorrectLog("True");
    }

    public void onFalseButtonClicked(View v){
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

    private void getCorrectLog(String correct){
        tableLayout.removeAllViews();

        Call<List<FaceLog>> call = jsonPlaceHolderApi.getFaceCorrectLog(correct);

        call.enqueue(new Callback<List<FaceLog>>() {
            @Override
            public void onResponse(Call<List<FaceLog>> call, Response<List<FaceLog>> response) {
                if (!response.isSuccessful()) {
//                    result_textview.setText("code: " + response.code());
                    Log.d("CorrectLog", Integer.toString(response.code()));
                    return;
                }

                List<FaceLog> logs = response.body();
                TableRow init_tableRow = new TableRow(getApplicationContext());
                init_tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                if (correct.equals("True")) {
                    String[] rows = {"Correct", "Date", "Name", "Similarity"};
                    addItemOnRow(tableLayout, init_tableRow, rows);

                    for (FaceLog log : logs) {
                        String[] log_contents = {log.getCorrect(), log.getDate(), log.getName(), log.getSimilarity()};

                        TableRow tableRow = new TableRow(getApplicationContext());
//                        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                        addItemOnRow(tableLayout, tableRow, log_contents);
                    }
                }

                else {
                    String[] rows = {"Correct", "Date"};
                    addItemOnRow(tableLayout, init_tableRow, rows);
                    List<String> name_list = new ArrayList<String>();

                    for (FaceLog log : logs) {
                        String[] log_contents = {log.getCorrect(), log.getDate()};
                        name_list.add(log.getName());

                        TableRow tableRow = new TableRow(getApplicationContext());
//                        tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 100));

                        addItemOnRow(tableLayout, tableRow, log_contents);
                        tableRow.setId(logs.indexOf(log));
                        tableRow.setClickable(true);
                        tableRow.setOnClickListener(new View.OnClickListener() {
                            public void onClick(View view) {
                                TableRow t = (TableRow) view;
                                TextView dateTextView = (TextView) t.getChildAt(1);
                                int index = t.getId();
                                String date = dateTextView.getText().toString();
//                                Toast.makeText(GetFaceLogActivity.this, Integer.toString(index), Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), GetStrangerActivity.class);
                                intent.putExtra("date", date);
                                intent.putExtra("name", name_list.get(index));
                                startActivity(intent);
                            }
                        });
                    }
                }
            }

            @Override
            public void onFailure(Call<List<FaceLog>> call, Throwable t) {
                Log.d("CorrectLog", t.getMessage());
            }
        });
    }

    
}