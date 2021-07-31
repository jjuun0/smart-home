package com.example.SmartHome.CCTV;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Bundle;

import java.io.File;
import java.net.URL;

import android.media.MediaPlayer;
import android.util.Log;
import android.widget.MediaController;
import android.widget.VideoView;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferListener;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferNetworkLossHandler;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferObserver;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferState;
import com.amazonaws.mobileconnectors.s3.transferutility.TransferUtility;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest;
import com.example.SmartHome.R;

public class GetVideoActivity extends AppCompatActivity {

//    https://lcw126.tistory.com/117

    VideoView videoView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_video);

        videoView = findViewById(R.id.videoView);

        Intent intent = getIntent();
        String folder = intent.getStringExtra("folder");
        String file = intent.getStringExtra("file");
        String bucketName = getString(R.string.bucket_name);
        String objectName = "cctv/" + folder + "/" + file + ".mp4";

        String accessKey = getString(R.string.accessKey);
        String secretKey = getString(R.string.secretKey);
        AWSCredentials myCredentials = new BasicAWSCredentials(accessKey, secretKey);
        AmazonS3 s3client = new AmazonS3Client(myCredentials);
        s3client.setRegion(Region.getRegion(Regions.AP_NORTHEAST_2));

        GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectName);
        URL objectURL = s3client.generatePresignedUrl(request);

        getWindow().setFormat(PixelFormat.TRANSLUCENT);
        MediaController mediaCtrl = new MediaController(this);
        mediaCtrl.setMediaPlayer(videoView);
        videoView.setMediaController(mediaCtrl);
        Uri clip = Uri.parse(objectURL.toString());

//        Log.d("video", clip.toString());
        videoView.setVideoURI(clip);
        videoView.requestFocus();
        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mediaPlayer) {
                //비디오 시작
                videoView.start();
            }
        });

//
//
//        //비디오뷰의 재생, 일시정지 등을 할 수 있는 '컨트롤바'를 붙여주는 작업
//        videoView.setMediaController(new MediaController(GetVideoActivity.this));
//
//        //VideoView가 보여줄 동영상의 경로 주소(Uri) 설정하기
//        videoView.setVideoURI(videoUri);
//
//        //동영상을 읽어오는데 시간이 걸리므로..
//        //비디오 로딩 준비가 끝났을 때 실행하도록..
//        //리스너 설정
//        videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//            @Override
//            public void onPrepared(MediaPlayer mediaPlayer) {
//                //비디오 시작
//                videoView.start();
//            }
//        });
//
//        videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
//            @Override
//            public boolean onError(MediaPlayer arg0, int arg1, int arg2) {
//                Log.d("video", "setOnErrorListener ");
//                return true;
//            }
//        });

    }

    //화면에 안보일때...
    @Override
    protected void onPause() {
        super.onPause();

        //비디오 일시 정지
        if (videoView != null && videoView.isPlaying()) videoView.pause();
    }

    //액티비티가 메모리에서 사라질때..
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //
        if (videoView != null) videoView.stopPlayback();
    }

//    private void downloadS3(String filename) {
//        // KEY and SECRET are gotten when we create an IAM user above
//        String accessKey = getString(R.string.accessKey);
//        String secretKey = getString(R.string.secretKey);
//        String bucketName = getString(R.string.bucket_name);
//        String folderName = "allowed";
//
//        AWSCredentials awsCredentials = new BasicAWSCredentials(accessKey, secretKey);
//        AmazonS3Client s3Client = new AmazonS3Client(awsCredentials, Region.getRegion(Regions.AP_NORTHEAST_2));
//
//        TransferUtility transferUtility = TransferUtility.builder().s3Client(s3Client).context(this).build();
//        TransferNetworkLossHandler.getInstance(this);
//
//        TransferObserver downloadObserver =
//                transferUtility.download(bucketName, filename, new File(getApplicationContext().getFilesDir(), filename));
//
//        downloadObserver.setTransferListener(new TransferListener() {
//
//            @Override
//            public void onStateChanged(int id, TransferState state) {
//                if (TransferState.COMPLETED == state) {
//                    // Handle a completed upload.
//                }
//            }
//
//            @Override
//            public void onProgressChanged(int id, long bytesCurrent, long bytesTotal) {
//                float percentDonef = ((float)bytesCurrent/(float)bytesTotal) * 100;
//                int percentDone = (int)percentDonef;
//                Log.d("downloadS3 onProgress", "   ID:" + id + "   bytesCurrent: " + bytesCurrent + "   bytesTotal: " + bytesTotal + " " + percentDone + "%");
//            }
//
//            @Override
//            public void onError(int id, Exception ex) {
//                // Handle errors
//                Log.d("downloadS3 onError", ex.getMessage());
//            }
//
//        });
//    }


}