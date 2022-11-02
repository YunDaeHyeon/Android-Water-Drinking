package com.capstone.waterdrinking.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.capstone.waterdrinking.R;

import java.util.Random;

public class MainActivity extends AppCompatActivity {

    // 새로고침, 초기화 버튼 & 물 무게에 따른 이모티콘 & 텀블러
    private ImageView refreshButton, recycleButton, weightFace, bottleImage;
    private TextView weightTextView, drinkingTextView; // 물의 무게, 마신 물의 무게가 출력되는 텍스트 뷰
    private Button moveAlarmActivity;
    private int waterWeight = 1000; // 더미데이터 (1000mL)
    private int drinkingWater = 0; // 더미데이터  (0mL)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        refreshButton = (ImageView) findViewById(R.id.refreshButton);
        weightFace = (ImageView) findViewById(R.id.weightFace);
        bottleImage = (ImageView) findViewById(R.id.bottleImage);
        recycleButton = (ImageView) findViewById(R.id.recycleButton);
        weightTextView = (TextView) findViewById(R.id.weightTextView);
        drinkingTextView = (TextView) findViewById(R.id.drinkingTextView);
        moveAlarmActivity = (Button) findViewById(R.id.moveAlarmActivity);

        // Preferences 선언 (MODE_PRIVATE = 읽기 쓰기)
        SharedPreferences pref = getSharedPreferences("water", Activity.MODE_PRIVATE);
        SharedPreferences.Editor editor = pref.edit();

        // Preferences 호출 - 앱 실행 시 pref.getInt()가 0이 아니면 데이터가 존재한다는 뜻
        if(pref.getInt("waterWeight",0) != 0 || pref.getInt("drinkingWater",0) != 0){
            waterWeight = pref.getInt("waterWeight",0);
            drinkingWater = pref.getInt("drinkingWater",0);
            weightTextView.setText("측정된 물의 양 : "+waterWeight+"mL");
            drinkingTextView.setText("지금까지 마신 물의 양 : "+drinkingWater+"mL");
            changeImage(drinkingWater);
        }

        // 새로고침 버튼
        refreshButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 텍스트 뷰 변경
                waterWeight = waterWeight - 100;
                drinkingWater = drinkingWater + 100;
                if(waterWeight < 0 || drinkingWater > 1000){
                    waterWeight = 1000;
                    drinkingWater = 0;
                    changeImage(drinkingWater);
                }
                weightTextView.setText("측정된 물의 양 : "+waterWeight+"mL");
                drinkingTextView.setText("지금까지 마신 물의 양 : "+drinkingWater+"mL");

                // Preferences 저장
                editor.putInt("waterWeight", waterWeight); // 불러온 물의 무게
                editor.putInt("drinkingWater", drinkingWater); // 마신 물의 총량
                editor.commit(); // 반영

                // 물 무게에 따른 이모티콘/텀블러 변경 호출
                changeImage(drinkingWater);
            }
        });

        // 초기화 클릭 시 Preference, 기존 데이터 초기화
        recycleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Preference 초기화
                editor.clear();
                editor.commit();
                // 기존 데이터 초기화
                waterWeight = 1000;
                drinkingWater = 0;
                weightTextView.setText("측정된 물의 양 : "+waterWeight+"mL");
                drinkingTextView.setText("지금까지 마신 물의 양 : "+drinkingWater+"mL");
                // 이모티콘, 텀블러 다시 그리기
                changeImage(drinkingWater);
            }
        });

        // 알람 설정
        moveAlarmActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AlarmActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    // 마신 물의 양에 따른 이모티콘, 텀블러(ImageView) 변경
    private void changeImage(int drinkingWater){
        if(drinkingWater < 300){
            // 300mL 이하
            weightFace.setImageResource(R.drawable.weight_0); // 이모티콘
            bottleImage.setImageResource(R.drawable.water_weight_0); // 텀블러
        }else if(drinkingWater < 700 && drinkingWater > 300){
            // 300mL ~ 700mL
            weightFace.setImageResource(R.drawable.weight_50);
            bottleImage.setImageResource(R.drawable.water_weight_50);
        }else if(drinkingWater > 700){
            // 700mL 이상
            weightFace.setImageResource(R.drawable.weight_100);
            bottleImage.setImageResource(R.drawable.watar_weight_100);
        }
    }
}