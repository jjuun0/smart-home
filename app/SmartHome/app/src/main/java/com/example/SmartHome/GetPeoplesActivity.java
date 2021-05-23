package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobile.client.AWSMobileClient;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.services.s3.AmazonS3Client;

import java.io.File;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class GetPeoplesActivity extends AppCompatActivity {

    private JsonPlaceHolderApi jsonPlaceHolderApi;
    TextView people_list_view;
//    private final String BASEURL = "https://9c39rad6qj.execute-api.ap-northeast-2.amazonaws.com/new/";
    private String BASEURL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_people);

        BASEURL = getString(R.string.request_url);

        people_list_view = findViewById(R.id.people_list_view);

        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);

    }
    
    public void onBackButtonClicked(View v){
        Toast.makeText(getApplicationContext(), "돌아가기 버튼을 눌렀습니다.", Toast.LENGTH_LONG);
        finish();
    }

    public void requestGetButtonClicked(View v){
        getPeoples();
    }

    private void getPeoples() {
        Call<List<People>> call = jsonPlaceHolderApi.getPeoples();

        call.enqueue(new Callback<List<People>>() {
            @Override
            public void onResponse(Call<List<People>> call, Response<List<People>> response) {
                if (!response.isSuccessful()) {
                    people_list_view.setText("code: " + response.code());
                    return;
                }
                people_list_view.setText(" ");

                List<People> peoples = response.body();
                Log.d("error", response.body().toString());

                for (People people : peoples) {
                    String content = "";
                    content += "Name: " + people.getName() + "\n";
//                    content += "Image_Name: " + people.getImage_Name() + "\n\n";
//                    content += "Image_url: " + people.getImage_url() + "\n\n";

                    people_list_view.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<People>> call, Throwable t) {
                people_list_view.setText(t.getMessage());
                Log.d("error", t.getMessage());
            }
        });
    }

    private void downloadS3(String filename) {
        // KEY and SECRET are gotten when we create an IAM user above
        String accessKey = "AKIASH3RQY4CEKY5YVMK";
        String secretKey = "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";
        String bucketName = "junfirstbucket";
        String folderName = "entered";

        BasicAWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3Client s3Client = new AmazonS3Client(credentials);

        TransferUtility transferUtility =
                TransferUtility.builder()
                        .context(getApplicationContext())
                        .awsConfiguration(AWSMobileClient.getInstance().getConfiguration())
                        .s3Client(s3Client)
                        .build();

        TransferObserver downloadObserver =
                transferUtility.download(bucketName + "/" + folderName + "/" + filename, new File(getApplicationContext().getFilesDir(), filename + ".jpg"));

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

                Log.d("Download", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
            }

        });
    }



}