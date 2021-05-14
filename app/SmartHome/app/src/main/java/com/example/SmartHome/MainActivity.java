package com.example.SmartHome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
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
import com.amazonaws.services.s3.model.CannedAccessControlList;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {
    private final String BASEURL = "https://9c39rad6qj.execute-api.ap-northeast-2.amazonaws.com/new/";
    private TextView textViewResult;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private ImageView imageView;
    private EditText nameText;
    private static final int REQUEST_CODE = 0;

    private String bucketName = "junfirstbucket";
    private String region = "ap-northeast-2";
    private String saveFolder = "allowed";
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.text);
        nameText = findViewById(R.id.name);

        Retrofit retrofit = new Retrofit.Builder()  // retrofit 객체 선언
                .baseUrl(BASEURL)
                .addConverterFactory(new NullOnEmptyConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())  // gson converter 생성, gson 는 json 을 자바 클래스로 바꾸는데 사용
                .build();

        jsonPlaceHolderApi = retrofit.create(JsonPlaceHolderApi.class);


        // https://lktprogrammer.tistory.com/188
        imageView = findViewById(R.id.image);
        imageView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        Button button = (Button)findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPeople(nameText.getText().toString());

                // imageView 에 있는 이미지를 bitmap 으로 가져오기  https://hello-bryan.tistory.com/66
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap img = drawable.getBitmap();

                file = saveBitmapToJpeg(MainActivity.this, img, nameText.getText().toString());
                uploadS3(file);
            }
        });

    }
    @Override // https://m.blog.naver.com/l5547/221845481754
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                try {
                    InputStream in = getContentResolver().openInputStream(data.getData());

                    Bitmap img = BitmapFactory.decodeStream(in);
                    in.close();

                    imageView.setImageBitmap(img);

//                    file = saveBitmapToJpeg(this, img, nameText.getText().toString());

                } catch (Exception e) {

                }
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "사진 선택 취소", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void getPeoples() {
        Call<List<People>> call = jsonPlaceHolderApi.getPeople();

        call.enqueue(new Callback<List<People>>() {
            @Override
            public void onResponse(Call<List<People>> call, Response<List<People>> response) {
                if (!response.isSuccessful()) {
                    textViewResult.setText("code: " + response.code());
                    return;
                }

                List<People> peoples = response.body();
                Log.d("error", response.body().toString());

                for (People people : peoples) {
                    String content = "";
                    content += "Name: " + people.getName() + "\n";
                    content += "Image_Name: " + people.getImage_Name() + "\n\n";
//                    content += "Image_url: " + people.getImage_url() + "\n\n";

                    textViewResult.append(content);
                }
            }

            @Override
            public void onFailure(Call<List<People>> call, Throwable t) {
                textViewResult.setText(t.getMessage());
                Log.d("error", t.getMessage());
            }
        });
    }

    private void createPeople(String name) {
        People people = new People(name, name +".jpg", "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + saveFolder +"/" + name + ".jpg");

        Call<People> call = jsonPlaceHolderApi.createPeople(people);

        call.enqueue(new Callback<People>() {
            @Override
            public void onResponse(Call<People> call, Response<People> response) {

                if (!response.isSuccessful()) {
                    textViewResult.setText("code: " + response.code());
                    Log.d("error", response.toString());

                    return;
                }
                if (response.code() == 200){
                    textViewResult.setText("Complete saving User data");
                    Log.d("debugging", response.toString());
                }

////                Log.d("error", response.body().string());
//                People postResponse = response.body();
//                Log.d("error", postResponse.toString());
//                String content = "";
//                content += "Code: " + response.code() + "\n";

//                content += "Name: " + postResponse.getName() + "\n";
//                content += "Image_Name: " + postResponse.getImage_Name() + "\n";
//                content += "Image_url: " + postResponse.getImage_url() + "\n";

//                textViewResult.setText(content);
            }

            @Override
            public void onFailure(Call<People> call, Throwable t) {
                textViewResult.setText(t.getMessage());
                Log.d("error", t.getMessage());
            }
        });

    }


    private void uploadS3(File file) {
        String accessKey = "AKIASH3RQY4CEKY5YVMK";
        String secretKey = "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";

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build();
        TransferNetworkLossHandler.getInstance(this);

        TransferObserver uploadObserver = transferUtility.upload(bucketName, saveFolder + "/" + file.getName(), file,  CannedAccessControlList.PublicRead);
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("android", "onStateChanged: " + id + ", " + state.toString());

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                Log.d("android", "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("android", ex.getMessage());
            }
        });
    }

    public static File saveBitmapToJpeg(Context context, Bitmap bitmap, String name){
        // http://pjkstory.blogspot.com/2016/05/android-bitmap.html
        File storage = context.getCacheDir(); // 이 부분이 임시파일 저장 경로

        String fileName = name + ".jpg";

        File tempFile = new File(storage,fileName);

        try{
            tempFile.createNewFile();  // 파일을 생성해주고

            FileOutputStream out = new FileOutputStream(tempFile);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 90 , out);  // 넘거 받은 bitmap을 jpeg(손실압축)으로 저장해줌

            out.close(); // 마무리로 닫아줍니다.

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return tempFile;
    }

}
