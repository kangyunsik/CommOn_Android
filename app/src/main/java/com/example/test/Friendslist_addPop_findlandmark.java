package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Friendslist_addPop_findlandmark extends Activity {
    String f_list;
    String id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_friendlist_popup);

        id = getIntent().getStringExtra("id");
        f_list = getIntent().getStringExtra("friendslist");
        TextView friendView = (TextView) findViewById(R.id.place_latlng);

        StringBuilder sb = new StringBuilder();
        String[] strings = getIntent().getStringExtra("friendslist").split(",");

        for (int i = 0; i < strings.length; i++)
            sb.append(strings[i] + "\n");
        Log.w("팝업", sb.toString());

        friendView.setText(sb.toString());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friendslist_popup_button_ok:
                Intent intent = new Intent();
                intent.putExtra("friendslist",f_list);
                intent.putExtra("id", id);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.friendslist_popup_button_cancel:
                finish();
                break;
        }
    }


}
