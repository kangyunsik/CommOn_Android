
package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.Marker;

import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import static com.example.test.R.*;

public class FriendslistActivity extends AppCompatActivity {
    public static final int POPUP_CODE = 12341;
    private GeoUtil geoUtil;
    private CustomChoiceListViewAdapter adapter;
    protected ListView listview;
    private CustomTask_landmark customTask_landmark;
    String id, target_id;
    String input;
    ArrayList<String> openFriendsList;
    JsonParsingService jps;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(layout.activity_friendslist);

        openFriendsList = new ArrayList<>();
        jps = new JsonParsingService();
        geoUtil = new GeoUtil();
        adapter = new CustomChoiceListViewAdapter();
        Intent intent = getIntent();

        id = intent.getStringExtra("id");
        target_id = intent.getStringExtra("target_id");
        listview = findViewById(R.id.friendslist);
        listview.setAdapter(adapter);
    }


    @Override
    public void onResume() {
        super.onResume();
        adapter = new CustomChoiceListViewAdapter();
        openFriendsList = new ArrayList<>();
        listview.setAdapter(adapter);
        String result = null;

        for (Marker marker : MapsActivity.friendMarker) {
            String[] strings = marker.getPosition().toString().replaceAll("lat/lng: ", "").replaceAll("\\(", "").replaceAll("\\)", "").split(",");
            openFriendsList.add(marker.getTitle());
            adapter.addItem(ContextCompat.getDrawable(this, drawable.ic_launcher_foreground),
                    marker.getTitle(),
                    geoUtil.getCurrentAddress(getApplicationContext(),strings));
        }

        CustomTask_friends customTask_friends = new CustomTask_friends();
        try {
            result = customTask_friends.execute(getIntent().getStringExtra("id"), "getfriendlist").get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }
        jps.friendsParsing(result);

        lb:
        for (FriendsViewItem fvi : jps.getFriendsViewItems()) {
            for (String string : openFriendsList) {
                if (string.equals(fvi.getId()))
                    continue lb;
            }
            adapter.addItem(ContextCompat.getDrawable(this, drawable.ic_launcher_foreground),
                    fvi.getId(),
                    "비공개");
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.find_landmark_button:
                StringBuilder sb = new StringBuilder();

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.isSelected(i)) {
                        sb.append(((ListViewItem) adapter.getItem(i)).getId()).append(",");
                    }
                }

                if (sb.toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "사용자를 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                    break;
                }

                Intent intent = new Intent(getApplicationContext(), Friendslist_addPop_findlandmark.class);
                intent.putExtra("friendslist", sb.toString());
                intent.putExtra("id", id);
                startActivityForResult(intent, POPUP_CODE);
                break;

            case R.id.add_friends:
                StringBuilder sb2 = new StringBuilder();

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.isSelected(i)) {
                        sb2.append(((ListViewItem) adapter.getItem(i)).getId()).append(",");
                    }
                }
                Intent intent2 = new Intent(getApplicationContext(), Friendslist_addPop_add.class);
                intent2.putExtra("friendslist", sb2.toString());
                intent2.putExtra("id", id);

                startActivityForResult(intent2, 2);
                Log.w("qottkftkzpehd", "check");
                Log.w(id, "check");


                intent2.putExtra("target_id", target_id);
                Log.w(target_id, "check");
                break;
            case R.id.delete_friends:
                StringBuilder sb3 = new StringBuilder();

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.isSelected(i)) {
                        sb3.append(((ListViewItem) adapter.getItem(i)).getId()).append(",");
                    }
                }
                if (sb3.toString().equals("")) {
                    Toast.makeText(getApplicationContext(), "사용자를 선택해야 합니다.", Toast.LENGTH_SHORT).show();
                    break;
                }

                Intent intent3 = new Intent(getApplicationContext(), Friendslist_addPop_del.class);
                intent3.putExtra("id", id);
                intent3.putExtra("friendslist", sb3.toString());
                startActivityForResult(intent3, 3);
                break;

            case R.id.chat_button:
                StringBuilder sb4 = new StringBuilder();

                for (int i = 0; i < adapter.getCount(); i++) {
                    if (adapter.isSelected(i)) {
                        sb4.append(((ListViewItem) adapter.getItem(i)).getId()).append(",");
                    }
                }

                Intent intent4 = new Intent(getApplicationContext(), Friendslist_addPop_chat.class);
                intent4.putExtra("id", id);
                intent4.putExtra("friendslist", sb4.toString());
                startActivityForResult(intent4, 4);
                Log.w("chat start : ", sb4.toString());

                break;
            default:
                throw new IllegalStateException("Unexpected value: " + view.getId());
        }
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == POPUP_CODE) {

            if (resultCode == RESULT_OK) {
                String f_list = data.getStringExtra("friendslist");
                String id = data.getStringExtra("id");
                String result = null;

                Log.w("f_list : ", f_list);
                lb:
                for (String selected : f_list.split(",")) {
                    for (String open : openFriendsList) {
                        if (open.equals(selected))
                            continue lb;
                    }
                    Toast.makeText(getApplicationContext(), "비공개된 친구를 설정할 수 없습니다.", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    result = new CustomTask_landmark().execute(MapsActivity.id, f_list).get();
                } catch (ExecutionException | InterruptedException e) {
                    e.printStackTrace();
                }


                Intent intent = new Intent();
                intent.putExtra("fri_list", f_list);
                intent.putExtra("id", id);
                if (result != null) {
                    intent.putExtra("landmark_string", result);
                    setResult(RESULT_OK, intent);
                }
                finish();
            }
        }
        //친구 추가 요청
        else if (requestCode == 2) {
            if (resultCode == RESULT_OK) {
                String result = data.getStringExtra("result");
                if (result.contains("NOTEXIST")) {
                    Toast.makeText(getApplicationContext(), "존재하지 않는 아이디입니다.", Toast.LENGTH_SHORT).show();
                } else if (result.contains("ISYOU")) {
                    Toast.makeText(getApplicationContext(), "본인 아이디입니다.", Toast.LENGTH_SHORT).show();
                } else if (result.contains("DUPLICATE")) {
                    Toast.makeText(getApplicationContext(), "이미 친구입니다.", Toast.LENGTH_SHORT).show();
                } else
                    Toast.makeText(getApplicationContext(), data.getStringExtra("targetid") + " 친구 추가 되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "취소됐습니다.", Toast.LENGTH_SHORT).show();
            }
        }
        //선택된 친구 삭제 요청
        else if (requestCode == 3) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(getApplicationContext(), data.getStringExtra("targetids") + " 친구 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "취소됐습니다.", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 4) {
            if (resultCode == RESULT_OK) {
                Log.w("테스트", "this is request 4 suc________________________________________________________" + id);
            }
        }
    }
}