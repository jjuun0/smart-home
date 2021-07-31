package com.example.SmartHome.CCTV;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
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

public class GetCCTVFilesActivity extends AppCompatActivity {
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private String BASEURL;

    TableLayout tableLayout;
    String date_folder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_cctv_files);

        BASEURL = getString(R.string.request_url);
        tableLayout = findViewById(R.id.cctv_table);
        TextView textView = findViewById(R.id.date_folder);

        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

        Intent intent = getIntent();
        date_folder = intent.getStringExtra("date");

        String date_text = "";
        String[] dates = date_folder.split("_");
        String[] time = {"년", "월", "일"};
        for(int i = 0; i < dates.length; i++){
            date_text += dates[i] + time[i] + " ";
        }

        textView.setText(date_text);
        getCCTVfiles(date_folder);
    }

    public void addItemOnRow(TableLayout tableLayout, TableRow tableRow, String name) {
        TextView textView = new TextView(getApplicationContext());
        textView.setText(name);
        textView.setGravity(Gravity.CENTER);
        TableRow.LayoutParams prms = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT,100);
        tableRow.addView(textView, prms);
        tableLayout.addView(tableRow);
    }

    private void getCCTVfiles(String date) {
        Call<List<String>> call = jsonPlaceHolderApi.getCCTVfiles(date);

        call.enqueue(new Callback<List<String>>() {
            @Override
            public void onResponse(Call<List<String>> call, Response<List<String>> response) {
                if (!response.isSuccessful()) {
                    Log.d("cctv_files", Integer.toString(response.code()));
                    return;
                }

                List<String> list = response.body();
//                Log.d("cctv_files", response.body().toString());

                for (String item : list) {
                    TableRow tableRow = new TableRow(getApplicationContext());
                    addItemOnRow(tableLayout, tableRow, item);

                    tableRow.setClickable(true);
                    tableRow.setOnClickListener(new View.OnClickListener() {
                        public void onClick(View view) {
                            Intent intent = new Intent(getApplicationContext(), GetVideoActivity.class);
                            intent.putExtra("folder", date_folder);
                            intent.putExtra("file", item);
                            startActivity(intent);
                        }
                    });

                }
            }

            @Override
            public void onFailure(Call<List<String>> call, Throwable t) {
                Log.d("cctv_files", t.getMessage());
            }
        });
    }
}