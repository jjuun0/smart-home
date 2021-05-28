package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetLogActivity extends AppCompatActivity {
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private String BASEURL;
    TableLayout tableLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_log);

        BASEURL = getString(R.string.request_url);

        tableLayout = findViewById(R.id.table);

        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
        getLog();
    }

    private void getLog() {
        // https://1d1cblog.tistory.com/140 table layout 사용하기
        Call<List<LogTable>> call = jsonPlaceHolderApi.getLog();

        call.enqueue(new Callback<List<LogTable>>() {
            @Override
            public void onResponse(Call<List<LogTable>> call, Response<List<LogTable>> response) {
                if (!response.isSuccessful()) {
//                    result_textview.setText("code: " + response.code());
                    Log.d("Log", Integer.toString(response.code()));
                    return;
                }

                List<LogTable> logs = response.body();

                for (LogTable log : logs) {
                    TableRow tableRow = new TableRow(getApplicationContext());
                    tableRow.setLayoutParams(new TableRow.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                    TextView date = new TextView(getApplicationContext());
                    date.setText(log.getDate());
                    date.setGravity(Gravity.CENTER);
                    tableRow.addView(date);

                    TextView correct = new TextView(getApplicationContext());
                    correct.setText(log.getCorrect());
                    correct.setGravity(Gravity.CENTER);
                    tableRow.addView(correct);

                    TextView image_name = new TextView(getApplicationContext());
                    image_name.setText(log.getImage_Name());
                    image_name.setGravity(Gravity.CENTER);
                    tableRow.addView(image_name);

                    TextView similarity = new TextView(getApplicationContext());
                    similarity.setText(log.getSimilarity());
                    similarity.setGravity(Gravity.CENTER);
                    tableRow.addView(similarity);

                    tableLayout.addView(tableRow);
                }
            }

            @Override
            public void onFailure(Call<List<LogTable>> call, Throwable t) {
                Log.d("Log", t.getMessage());
            }
        });
    }
}