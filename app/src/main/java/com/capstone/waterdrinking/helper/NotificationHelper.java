package com.capstone.waterdrinking.helper;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;

import com.capstone.waterdrinking.R;
import com.capstone.waterdrinking.activity.AlarmActivity;

public class NotificationHelper extends ContextWrapper {
        public static final String channelId = "water_drinking_alarm_channel_id";
        public static final String channelName = "텀블러";

        // 노티피케이션 선언
        private NotificationManager notificationManager;

        private Intent intent;

        // 생성자 할당
        public NotificationHelper(Context base) {
            super(base);

            // 안드로이드 버전이 오레오 이상이면 노티피케이션 채널 생성
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                // 알람 클릭 시 해당 액티비티가 실행될 수 있도록 플래그 설정
                intent = new Intent(this, AlarmActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                createNotificationChannel();
            }
        }

        // RequiresApi 어노테이션은 지정한 버전보다 낮을 경우 컴파일 에러 발생
        @RequiresApi(api = Build.VERSION_CODES.O)
        public void createNotificationChannel(){
            NotificationChannel channel = new NotificationChannel(channelId, channelName,
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.enableLights(true);
            channel.enableVibration(true);
            channel.setLightColor(com.google.android.material.R.color.design_default_color_on_primary);
            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

            getManager().createNotificationChannel(channel);
        }

        public NotificationManager getManager(){
            // 노티피케이션이 존재하지 않다면
            if(notificationManager == null){
                // 시스템 서비스에 노티피케이션 추가
                notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            }
            return notificationManager;
        }

        // 노티피케이션 빌드
        public NotificationCompat.Builder getChannelNotification(){
            PendingIntent pendingIntent = PendingIntent.getActivity(this, 1, intent, PendingIntent.FLAG_IMMUTABLE );
            return new NotificationCompat.Builder(getApplicationContext(), channelId)
                    .setSmallIcon(R.drawable.ic_launcher_foreground) // 작은 아이콘 설정
                    .setContentTitle("텀블러 어플") // 알람 타이틀 지정
                    .setContentText("물 먹을 시간이에요 ><") // 알람 내용 지정
                    .setStyle(new NotificationCompat.BigTextStyle()
                            .bigText("귀여운 유경이 보러 오세요 ~~")) // 알람 내용 확장 (필수 X)
                    .setContentIntent(pendingIntent) // 알람 클릭시 지정한 인탠트 실행
                    .setAutoCancel(true) // AutoCancel이 true이면 클릭 시 알람이 삭제된다.
                    .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE); // 사운드, 진동 설정
        }

        // 노티피케이션 파괴
        private void destroyNotification(int id){
            NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancel(id);
        }
}
