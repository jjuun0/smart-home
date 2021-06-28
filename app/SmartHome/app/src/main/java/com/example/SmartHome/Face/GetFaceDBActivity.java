package com.example.SmartHome.Face;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.example.SmartHome.JsonPlaceHolderApi;
import com.example.SmartHome.R;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GetFaceDBActivity extends AppCompatActivity {
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    RadioGroup people_list_view;
    private String BASEURL;
    RadioButton people_radio;
    ImageView people_image;
    List<FaceDB> faceDBS;
    FaceDB checked_faceDB;
    TextView result_textview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_face_db);

        BASEURL = getString(R.string.request_url);

        people_list_view = findViewById(R.id.people_list_view);
        people_radio = findViewById(R.id.people_radio);
        people_image = findViewById(R.id.people_image);
        result_textview = findViewById(R.id.result);
        result_textview.setText("조회 버튼을 눌러주세요.");
        people_list_view.setVisibility(View.INVISIBLE);


        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
//                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);



        // http://blog.naver.com/PostView.nhn?blogId=kimsh2244&logNo=221070877167 change image by checked radio button
        people_list_view.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                checked_faceDB = faceDBS.get(checkedId);
                File imgFile = new  File(getApplicationContext().getFilesDir(), checked_faceDB.getImage_Name());  // 'data/data/패키지/files' 에 이미지 저장
                if(imgFile.exists()){
                    Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                    people_image.setImageBitmap(imgBitmap);
                    result_textview.setText("삭제하실 항목을 선택하여 삭제 버튼을 눌러주세요");
                }
                else{
                    Toast.makeText(GetFaceDBActivity.this, "이미지 파일이 없습니다.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    public void requestAddButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), AddFaceDBActivity.class);
        startActivity(intent);
    }

    public void requestLogButtonClicked(View v){
        Intent intent = new Intent(getApplicationContext(), GetFaceLogActivity.class);
        startActivity(intent);
    }

    public void requestGetButtonClicked(View v){
        getPeoples();
        result_textview.setText("삭제하실 항목을 선택하여 삭제 버튼을 눌러주세요");
    }

    private void getPeoples() {
        people_list_view.setVisibility(View.VISIBLE);
        Call<List<FaceDB>> call = jsonPlaceHolderApi.getFaceDB();

        call.enqueue(new Callback<List<FaceDB>>() {
            @Override
            public void onResponse(Call<List<FaceDB>> call, Response<List<FaceDB>> response) {
                if (!response.isSuccessful()) {
                    result_textview.setText("code: " + response.code());
                    return;
                }

                faceDBS = response.body();

                people_list_view.removeAllViews();

                for (FaceDB faceDB : faceDBS) {
                    // https://blog.daum.net/andro_java/1212 radio button 동적으로 추가
                    RadioButton people_radio_button = new RadioButton(getApplicationContext());
                    people_radio_button.setText(faceDB.getName());
                    people_radio_button.setTextColor(getResources().getColor(R.color.black));
                    people_radio_button.setId(faceDBS.indexOf(faceDB));
                    RadioGroup.LayoutParams rprms= new RadioGroup.LayoutParams(RadioGroup.LayoutParams.WRAP_CONTENT, RadioGroup.LayoutParams.WRAP_CONTENT);
                    people_list_view.addView(people_radio_button, rprms);
                    downloadS3(faceDB.getImage_Name());

                }
            }

            @Override
            public void onFailure(Call<List<FaceDB>> call, Throwable t) {
                Log.d("getpeoples", t.getMessage());
            }
        });
    }

    private void downloadS3(String filename) {
        // KEY and SECRET are gotten when we create an IAM user above
        String accessKey = getString(R.string.accessKey);
        String secretKey = getString(R.string.secretKey);
        String bucketName = getString(R.string.bucket_name);
        String folderName = "allowed";

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build();
        TransferNetworkLossHandler.getInstance(this);

        TransferObserver downloadObserver =
                transferUtility.download(bucketName, folderName + "/" + filename, new File(getApplicationContext().getFilesDir(), filename));

        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                Log.d("downloadS3 onProgress", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Log.d("downloadS3 onError", ex.getMessage());
            }

        });
    }

    public void requestDeleteButtonClicked(View v){
        deletePeople();
    }

    private void deletePeople(){
        Call<FaceDB> call = jsonPlaceHolderApi.deleteFaceDB(checked_faceDB.getName());

        call.enqueue(new Callback<FaceDB>() {
            @Override
            public void onResponse(Call<FaceDB> call, Response<FaceDB> response) {

                if (!response.isSuccessful()) {
                    result_textview.setText("error code: " + response.code());
                    Log.d("delete onResponse", response.toString());
                    return;
                }

                if (response.code() == 200){  // api 에서 리턴해주는 값을 가져오지 못함..
                    // {
                    //      "DynamoDB delete" : true or false,
                    //      "S3 delete" : true or false
                    // }
                    result_textview.setText("삭제가 완료되었습니다");
                    Log.d("delete onResponse", response.body().toString());
                }
            }

            @Override
            public void onFailure(Call<FaceDB> call, Throwable t) {
                result_textview.setText(t.getMessage());
                Log.d("delete onFailure", t.getMessage());
            }
        });
    }
}