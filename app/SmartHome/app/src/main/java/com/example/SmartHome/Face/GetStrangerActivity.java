package com.example.SmartHome.Face;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.example.SmartHome.MainActivity;
import com.example.SmartHome.R;

import java.io.File;

public class GetStrangerActivity extends AppCompatActivity {
    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_stranger);

        TextView date_textview = findViewById(R.id.date);
        imageView = findViewById(R.id.stranger_image);

        Intent intent = getIntent();

        String date = intent.getStringExtra("date");
        String name = intent.getStringExtra("name");
        String filename = name + "_" + date;
        downloadS3(filename);

        String date_text = "";
        String[] dates = date.split("-");
        String[] time = {"년", "월", "일", "", "시", "분", "초"};

        for(int i = 0; i < dates.length; i++){
            date_text += dates[i] + time[i] + " ";
        }

        date_textview.setText(date_text);
    }

    private void setImageView(String filename){
        File imgFile = new File(getApplicationContext().getFilesDir(), filename + ".jpg");  // 'data/data/패키지/files' 에 이미지 저장

        Toast.makeText(GetStrangerActivity.this, filename+".jpg", Toast.LENGTH_SHORT).show();
        if(imgFile.exists()){
            Bitmap imgBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
            imageView.setImageBitmap(imgBitmap);
        }
        else{
            Toast.makeText(GetStrangerActivity.this, "이미지 파일이 없습니다.", Toast.LENGTH_SHORT).show();
        }
    }

    private void downloadS3(String filename) {
        // KEY and SECRET are gotten when we create an IAM user above
        String accessKey = getString(R.string.accessKey);
        String secretKey = getString(R.string.secretKey);
        String bucketName = getString(R.string.bucket_name);
        String folderName = getString(R.string.face_enter_folder);

        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));

        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build();
        TransferNetworkLossHandler.getInstance(this);

        TransferObserver downloadObserver =
                transferUtility.download(bucketName, folderName + "/" + filename + ".jpg", new File(getApplicationContext().getFilesDir(), filename + ".jpg"));

        downloadObserver.setTransferListener(new TransferListener() {

            @Override
            public void onStateChanged(int id, TransferState state) {
                if (TransferState.COMPLETED == state) {
                    // Handle a completed upload.
                    setImageView(filename);
                }
            }

            @Override
            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
                int percentDone = (int)percentDonef;
                Log.d("stranger", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
            }

            @Override
            public void onError(int id, Exception ex) {
                // Handle errors
                Log.d("stranger onError", ex.getMessage());
            }

        });
    }


}