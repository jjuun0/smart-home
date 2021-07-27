package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.SmartHome.Face.FaceDB;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class AdminActivity extends AppCompatActivity {
    private String BASEURL;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    TextView resultTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        resultTV = findViewById(R.id.admin_result);

        BASEURL = getString(R.string.request_url);
        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);
    }

    public void AllowButtonClicked(View v) {
        PutItemDB();
    }

    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Calendar calendar = Calendar.getInstance();
        Date date = calendar.getTime();
        format.setTimeZone(TimeZone.getTimeZone("Asia/Seoul"));
        String dateResult = format.format(date);
        return dateResult;
    }


    public void PutItemDB() {
        String time = getDate();
        Admin admin = new Admin("True", time);

        Call<Admin> call = jsonPlaceHolderApi.addAdmin(admin);

        call.enqueue(new Callback<Admin>() {
            @Override
            public void onResponse(Call<Admin> call, Response<Admin> response) {

                if (!response.isSuccessful()) {
                    resultTV.setText("code: " + response.code());
                    Log.d("admin", response.toString());

                }

                if (response.code() == 200){
                    resultTV.setText("success add to Admin DB");
                    Log.d("admin", response.toString());
                }
            }

            @Override
            public void onFailure(Call<Admin> call, Throwable t) {
                resultTV.setText(t.getMessage());
                Log.d("admin", t.getMessage());
            }
        });
    }

}