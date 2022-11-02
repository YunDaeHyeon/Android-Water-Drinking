package com.capstone.waterdrinking.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.DialogFragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.capstone.waterdrinking.R;
import com.capstone.waterdrinking.fragment.TimePickerFragment;
import com.capstone.waterdrinking.helper.NotificationHelper;
import com.capstone.waterdrinking.receiver.AlertReceiver;

import java.text.DateFormat;
import java.util.Calendar;

/*
    알람 액티비티, 해당 액티비티는 Android 지원 라이브러리의 NotificationCompat API 사용하여 푸시 알림을 구현한다.
    build.gradle(Module)에 의존성 추가 : implementation "com.android.support:support-compat:28.0.0"
    단, API 14 이하만 의존성 추가.
    레퍼런스 : https://developer.android.com/training/notify-user/build-notification?hl=ko
    레이아웃 : activity_main.xml
    Notification(노티피케이션, 알림)은 아래와 같은 로직으로 진행된다.
    1. 알림 콘텐츠 설정 및 알람 탭 작업 설정 (Notification.Builder, PendingIntent 사용)
    2. 채널 만들기, 중요도 설정
    3. 알림 표시
    단, 노티피케이션은 API 26(오레오) 이상부터 채널이 필요하다.
    이를 위해 OS가 오레오 이상인지, 이하인지 구별하여 알람을 구현하자.
    노티피케이션으로 구현된 알람이 출력될 때 카카오톡 알람이 오는 것 처럼 팝업으로 띄우려고 했는데
    코드 구현 방법을 몰라, 우선 디바이스 내 권한을 임의로 켰다.
    1. 설정 -> 애플리케이션 -> 제작한 어플리케이션 이름 -> 애플리케이션 설정(알림) -> 제작한 채널 이름 -> 팝업으로 표시 ON
*/

// AppCompatActivity 상속
// TimePickerDialog 인터페이스 사용
public class AlarmActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    private Button backButton, alarmCancelButton;
    private ImageView alarmSettingButton;
    private TextView alarmTextView;

    // 노티피케이션(알람) 구현 클래스 선언
    private NotificationHelper notificationHelper;
    // 노티피케이션(알람) 빌더
    private NotificationCompat.Builder builder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        backButton = (Button) findViewById(R.id.backButton);
        alarmCancelButton = (Button) findViewById(R.id.alarmCancelButton);
        alarmSettingButton = (ImageView) findViewById(R.id.alarmSettingButton);
        alarmTextView = (TextView) findViewById(R.id.alarmTextView);

        // 노티피케이션(알람)을 해당 액티비티에 지정
        notificationHelper = new NotificationHelper(getApplicationContext());

        // 알람 버튼 클릭
        alarmSettingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment timePicker = new TimePickerFragment();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        // 알람 취소 버튼 클릭
        alarmCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cancelAlarm();
            }
        });

        // 뒤로가기 버튼 클릭
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // 해당 메소드는 사용자가 시간을 설정하면 호출
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // 캘린더 객체 호출
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay); // 시간 설정
        calendar.set(Calendar.MINUTE, minute); // 분 설정
        calendar.set(Calendar.SECOND, 0); // 초 설정

        // 화면에 시간 뿌리기
        updateTimeText(calendar);
        // 알림 설정
        startAlarm(calendar);
    }

    private void updateTimeText(Calendar calendar){
        String timeText = "알람 시간 : ";
        timeText += DateFormat.getTimeInstance(DateFormat.SHORT).format(calendar.getTime());
        alarmTextView.setText(timeText);
    }

    // 알람 시작
    private void startAlarm(Calendar calendar){
        // 알람매니저 호출 - 시스템 서비스에 알람 서비스 추가
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        // 현재 컨텍스트와 리시버를 인텐트로 전달
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        /*
        PendingIntent는 intent를 바로 수행하지 않고, 특정 시점에 수행시킨다.
        이때 특정 시점은 !! 앱이 구동되고 있지 않을 때 !!
        즉, 알람 매니저를 통해 어플리케이션이 구동되고 있지 않아도 getSystemService로 알람 서비스를 지정하였기에
        알람 매니저로 지정된 시간에 intent를 수행시킨다.
        PendingIntent.getBroadcast는 결론적으로 특정 시점에 브로드캐스트 실행
         */
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
        // 만약, 캘린더 객체보다 매개변수로 넘어온 캘린더 객체가 먼저 생성되다면
        if(calendar.before(Calendar.getInstance())){
            calendar.add(Calendar.DATE, 1); // 날짜에 하루 추가하기
        }
        // 알람 매니저로 설정한 시간에 기기의 절전모드를 해제(RTC_WAKEUP)시켜 대기중인 인텐트(PendingIntent)를 실행한다.
        // setExact 메서드는 지정된 시간에 알람이 전달되도록 예약한다.
        // setExact(int type, long triggerAtMillis, PendingIntent operation)
        // 알람 타입, 캘린더에서 설정한 시간을 ms로 변환한 값, 대기중인 인텐트를 인수로 넘긴다.
        // RTC_WAKEUP를 사용하기 위해서는 퍼미션을 추가해야한다. <uses-permission android:name="android.permission.SCHEDULE_EXACT_ALARM" />
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
        Toast.makeText(getApplicationContext(), "알람이 설정되었습니다.", Toast.LENGTH_LONG).show();
    }

    // 알람 취소
    private void cancelAlarm(){
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getApplicationContext(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 1, intent, 0);
        alarmManager.cancel(pendingIntent);
        alarmTextView.setText("알람이 취소되었습니다.");
        Toast.makeText(getApplicationContext(), "알람이 취소되었습니다.", Toast.LENGTH_LONG).show();
    }

}