package com.example.test;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.concurrent.ExecutionException;

public class Friendslist_addPop_add extends Activity {
    String id;
    TextView targetid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_friendslist_popup_add);

        targetid = (TextView) findViewById(R.id.addpopup_id);
        id = getIntent().getStringExtra("id");
        //StringBuilder sb = new StringBuilder();
        //String[] strings = getIntent().getStringExtra("friendslist").split(",");

        //for (int i = 0; i < strings.length; i++)
        //    sb.append(strings[i] + "\n");
        //Log.w("팝업", sb.toString());

        //friendView.setText(sb.toString());
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.friendslist_addpopup_button_ok:
                String ti = targetid.getText().toString();
                Friendlist_addFriend fadd = new Friendlist_addFriend();
                try {
                    String result = fadd.execute(id,ti,"1","0").get();
                    setResult(RESULT_OK, new Intent().putExtra("targetid",ti).putExtra("result",result));
                } catch (ExecutionException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                finish();
                break;
            case R.id.friendslist_addpopup_button_cancel:
                finish();
                break;
        }
    }
}

