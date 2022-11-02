package com.capstone.waterdrinking.receiver;

/*
BroadcastReceiver를 상속받은 클래스를 정의하기 위해 AndroidManifest.xml에
receiver 태그로 등록한다. <receiver android:name=".receiver.AlertReceiver"/>
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import com.capstone.waterdrinking.helper.NotificationHelper;

// 알람 리시버
public class AlertReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        NotificationHelper notificationHelper = new NotificationHelper(context);
        // 노티피케이션 빌더에 채널 가져오기
        NotificationCompat.Builder builder = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, builder.build());
    }
}