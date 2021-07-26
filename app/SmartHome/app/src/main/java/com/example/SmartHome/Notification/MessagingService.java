package com.example.SmartHome.Notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.SmartHome.MainActivity;
import com.example.SmartHome.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class MessagingService extends FirebaseMessagingService {
    private static final String[] topics = {"/topics/notify"};  // 주제 경로 설정

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        makeNotification(remoteMessage);
    }

    private void makeNotification(RemoteMessage remoteMessage) {
        try {
            int notificationId = -1;
            Context mContext = getApplicationContext();

            Intent intent = new Intent(this, MainActivity.class);
            intent.setAction(Intent.ACTION_MAIN);  // 최초의 액티비티로서 실행할 수 있게 한다.
            intent.addCategory(Intent.CATEGORY_LAUNCHER);  // 어플리케이션 런쳐로 실행되는 분류

            String title = remoteMessage.getData().get("title");
            String message = remoteMessage.getData().get("body");
            String topic = remoteMessage.getFrom();


            // 알림(Notification)을 관리하는 NotificationManager 얻어오기
            NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            // 알림을 만들어내는 Builder 객체 생성
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this, "10001");
            // 오레오 버전 이하
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                builder.setVibrate(new long[] {200, 100, 200});
            }

            builder.setSmallIcon(R.drawable.ic_warning)
                    .setAutoCancel(true)  // 알림 터치시 자동으로 삭제
                    .setDefaults(Notification.DEFAULT_SOUND)  // 알림 발생시 진동, 사운드 설정
                    .setContentTitle(title)
                    .setContentText(message);

            if (topic.equals(topics[0])) {
                notificationId = 0;
            }

            if (notificationId >= 0) {
                PendingIntent pendingIntent = PendingIntent.getActivity(this, notificationId, intent, PendingIntent.FLAG_CANCEL_CURRENT);
                builder.setContentIntent(pendingIntent);
                notificationManager.notify(notificationId, builder.build());
            }

        } catch (NullPointerException nullException) {
            Toast.makeText(getApplicationContext(), "알림에 오류가 발생했습니다.", Toast.LENGTH_SHORT).show();
            Log.e("error Notify", nullException.toString());
        }
    }

    @Override
    public void onNewToken(String s) {
        Log.e("token?", s);
        super.onNewToken(s);
    }
}
