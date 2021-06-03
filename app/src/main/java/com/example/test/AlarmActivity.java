package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class AlarmActivity extends Activity {
    private String initUser;
    private String users;
    private String place_name;
    private double lati;
    private double longi;

    TextView initUserTV;
    TextView name;
    TextView latlng;
    TextView usersTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.alarm_popup);

        String title;
        String body;

        name = findViewById(R.id.alarm_place_name);
        latlng = findViewById(R.id.alarm_place_latlng);
        initUserTV = findViewById(R.id.alarm_init_user);
        usersTV = findViewById(R.id.alarm_friends);

        title = getIntent().getStringExtra("title");
        body = getIntent().getStringExtra("body");

        settingValues(title, body);

        name.setText(place_name);
        latlng.setText(lati + "/" + longi);
        initUserTV.setText(initUser);
        usersTV.setText(users);
    }

    private void settingValues(String title, String body) {
        place_name = body.split("위치 : ")[1].split("\n")[0];
        initUser = title.split("님의 미팅")[0];
        lati = Double.parseDouble(body.split("좌표 : ")[1].split("/")[0]);
        longi = Double.parseDouble(body.split("좌표 : ")[1].split("/")[1]);
        users = body.split("참가자 : ")[1].split("\n")[0];
    }

    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.alarm_ok:
                intent = new Intent();
                intent.putExtra("initUser",initUser);
                intent.putExtra("users",users);
                intent.putExtra("place_name",place_name);
                intent.putExtra("lati",lati);
                intent.putExtra("longi",longi);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.alarm_cancel:
                finish();
                break;
        }
    }
}
