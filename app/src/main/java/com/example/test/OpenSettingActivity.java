package com.example.test;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.maps.model.Marker;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class OpenSettingActivity extends AppCompatActivity {
    private FriendsOpenViewAdapter adapter;
    protected static ListView listview;
    private JsonParsingService jps;
    private ArrayList<FriendsViewItem> friendsArray;
    private MyUtil myUtil;
    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_setting);

        id = getIntent().getStringExtra("id");
        jps = new JsonParsingService();
        myUtil = new MyUtil();
        String result = null;

        CustomTask_friends customTask_friends = new CustomTask_friends();
        try {
            result = customTask_friends.execute(getIntent().getStringExtra("id"),"getfriendlist").get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        adapter = new FriendsOpenViewAdapter();
        listview = (ListView) findViewById(R.id.friendslist);
        listview.setAdapter(adapter);

        if(result!=null)
            Log.d("공개",result);

        jps.friendsParsing(result);
        friendsArray = jps.getFriendsViewItems();

        for(FriendsViewItem fvi : friendsArray)
            adapter.addItem(ContextCompat.getDrawable(this, R.drawable.ic_launcher_foreground), fvi.getId(),fvi.isChecked() );

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.setting_button:
                ArrayList<FriendsViewItem> after_fvis = new ArrayList<>();
                FriendsViewItem Item;

                String option = null;

                for(int i=0;i<adapter.getCount();i++) {
                    Item = new FriendsViewItem();
                    Item.setIcon(ContextCompat.getDrawable(this,R.drawable.ic_launcher_foreground));
                    Item.setChecked(adapter.isSelected(i));
                    Item.setId(((FriendsViewItem)adapter.getItem(i)).getId());
                    after_fvis.add(Item);
                }

                for(FriendsViewItem fvi : myUtil.getDiffViewItem(friendsArray,after_fvis)){
                    CustomTask_optionset customTask_optionset = new CustomTask_optionset();
                    option = fvi.isChecked() ? "1" : "0";
                    customTask_optionset.execute(id,fvi.getId(),option);
                }

                friendsArray = after_fvis;
                Toast.makeText(getApplicationContext(),"적용되었습니다.",Toast.LENGTH_LONG).show();
                finish();
                break;
            case R.id.cancel_button:
                finish();
                break;
        }
    }
}

