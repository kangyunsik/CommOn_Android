package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

public class Friendslist_addPop_del extends Activity {
    String[] targetid;
    String targetids;
    String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_freindslist_popup_del);

        TextView friendView = (TextView) findViewById(R.id.result_popup_friendlist_del);

        id = getIntent().getStringExtra("id");
        targetids = getIntent().getStringExtra("friendslist");
        targetid = targetids.split(",");

        friendView.setText(targetids);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friendslist_delpopup_button_ok:
                for (String string : targetid) {
                    Friendlist_deleteFriend fdel = new Friendlist_deleteFriend();
                    fdel.execute(id, string);
                }

                setResult(RESULT_OK, new Intent().putExtra("targetids", targetids));
                finish();
                break;
            case R.id.friendslist_delpopup_button_cancel:
                finish();
                break;
        }
    }
}

