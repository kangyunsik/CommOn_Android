package com.example.test;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import android.os.Bundle;
//import android.util.Log;
//import android.view.View;
//import android.widget.Button;
//import android.widget.EditText;
//
//public class addFriend_Activity extends AppCompatActivity {
//
//    private EditText addpopup_id;
//    private Button friendslist_addpopup_button;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_add_friend);
//
//        Log.w("target_id~~", "ㅅㅅ");
//        addpopup_id = findViewById(R.id.addpopup_id);
//
//        friendslist_addpopup_button = findViewById(R.id.friendslist_addpopup_button_ok);
//        friendslist_addpopup_button.setOnClickListener( new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String target_id = "target";
//                addFriend(target_id);
//////////////////////////
//            }
//        });
//    }
//
//    public void addFriend(String target_id) {
//        Log.w("target_id~~", "ㅅㅅ");
//    }
//}