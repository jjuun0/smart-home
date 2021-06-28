package com.example.SmartHome.Face;

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
import com.example.SmartHome.JsonPlaceHolderApi;
import com.example.SmartHome.NullOnEmptyConverterFactory;
import com.example.SmartHome.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddFaceDBActivity extends AppCompatActivity {
    private String BASEURL;
    private TextView textViewResult;
    private JsonPlaceHolderApi jsonPlaceHolderApi;
    private ImageView imageView;
    private EditText nameText;
    private static final int REQUEST_CODE = 0;

    private String bucketName;
    private String region;
    private String saveFolder;
    private File file;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face_db);

        BASEURL = getString(R.string.request_url);
        bucketName = getString(R.string.bucket_name);
        region = getString(R.string.region);
        saveFolder = "allowed";

        textViewResult = findViewById(R.id.text);
        nameText = findViewById(R.id.setname);

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

        Button save_button = (Button)findViewById(R.id.save_button);
        save_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createPeople(nameText.getText().toString());

                // imageView 에 있는 이미지를 bitmap 으로 가져오기  https://hello-bryan.tistory.com/66
                BitmapDrawable drawable = (BitmapDrawable) imageView.getDrawable();
                Bitmap img = drawable.getBitmap();

                file = saveBitmapToJpeg(AddFaceDBActivity.this, img, nameText.getText().toString());
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

    private void createPeople(String name) {
        FaceDB faceDB = new FaceDB(name, name +".jpg", "https://" + bucketName + ".s3." + region + ".amazonaws.com/" + saveFolder +"/" + name + ".jpg");

        Call<FaceDB> call = jsonPlaceHolderApi.createFaceDB(faceDB);

        call.enqueue(new Callback<FaceDB>() {
            @Override
            public void onResponse(Call<FaceDB> call, Response<FaceDB> response) {

                if (!response.isSuccessful()) {
                    textViewResult.setText("code: " + response.code());
                    Log.d("create", response.toString());

                    return;
                }
                if (response.code() == 200){
                    textViewResult.setText("추가가 완료되었습니다");
                    Log.d("create", response.toString());
                }
            }

            @Override
            public void onFailure(Call<FaceDB> call, Throwable t) {
                textViewResult.setText(t.getMessage());
                Log.d("create", t.getMessage());
            }
        });

    }


    private void uploadS3(File file) {
        String accessKey = getString(R.string.accessKey);
        String secretKey = getString(R.string.secretKey);

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build();
        TransferNetworkLossHandler.getInstance(this);

        TransferObserver uploadObserver = transferUtility.upload(bucketName, saveFolder + "/" + file.getName(), file,  CannedAccessControlList.PublicRead);
        uploadObserver.setTransferListener(new TransferListener() {
            @Override
            public void onStateChanged(int id, TransferState state) {
                Log.d("uploads3", "onStateChanged: " + id + ", " + state.toString());

            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float) bytesCurrent / (float) bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                Log.d("uploads3", "ID:" + id + " bytesCurrent: " + bytesCurrent + " bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                Log.e("uploads3", ex.getMessage());
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
