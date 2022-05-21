package com.example.duet.util;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.duet.R;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

public class CloudMessage extends FirebaseMessagingService {
    private static final String DEFAULT = "DEFAULT";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        String title = remoteMessage.getNotification().getTitle();
        String text = remoteMessage.getNotification().getBody();

        createNotificationChannel(DEFAULT, "default channel", NotificationManager.IMPORTANCE_HIGH);
        createNotification(DEFAULT, 1, title, text);

        super.onMessageReceived(remoteMessage);
    }

    void createNotificationChannel(String channelId, String channelName, int importance)
    {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notificationManager.createNotificationChannel(new NotificationChannel(channelId, channelName, importance));
        }
    }

    void createNotification(String channelId, int id, String title, String text)
    {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setPriority(NotificationCompat.PRIORITY_HIGH)          // Head Up Display를 위해 PRIORITY_HIGH 설정
                .setSmallIcon(R.drawable.ic_launcher_foreground)        // 알림시 보여지는 아이콘. 반드시 필요
                .setContentTitle(title)
                .setContentText(text)
                //.setTimeoutAfter(1000)    // 지정한 시간 이후 알림 삭제
                //.setStyle(new NotificationCompat.BigTextStyle().bigText(text))          // 한줄 이상의 텍스트를 모두 보여주고 싶을때 사용
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE);  // 알림시 효과음, 진동 여부

        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(id, builder.build());
    }

    void destroyNotification(int id)
    {
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(id);
    }
}