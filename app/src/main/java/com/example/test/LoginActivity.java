package com.example.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

public class LoginActivity extends AppCompatActivity {


    private EditText login_email, login_password;
    private Button login_button, join_button;
    String id, pw;
    String input;
    String tok = null;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_login );
        
        login_email = findViewById( R.id.login_email );
        login_password = findViewById( R.id.login_password );
        join_button = findViewById( R.id.join_button );
        
        login_email.setText("test4");               //  테스트. 하드코딩
        login_password.setText("test4");            //  테스트. 하드코딩
        
        try{
            if(getIntent().getStringExtra("title").contains("미팅")){
                login_email.setText(getIntent().getStringExtra("rec_id"));
                login_email.setFocusableInTouchMode(false);
                login_email.setOnClickListener(view -> Toast.makeText(getApplicationContext(),"초대받은 아이디만 로그인 가능합니다.",Toast.LENGTH_SHORT).show());
                Toast.makeText(getApplicationContext(),"미팅 요청입니다. 다시 로그인하세요.",Toast.LENGTH_LONG).show();
            }
        }catch(Exception ignored){}



        join_button.setOnClickListener(view -> {
            Intent intent = new Intent( LoginActivity.this, JoinActivity.class );
            startActivity( intent );
        });


        login_button = findViewById( R.id.login_button );
        login_button.setOnClickListener(view -> {
            String root = "login";
            login(root);
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(task -> {
                    if (!task.isSuccessful()) {
                        Log.d("device", "Fetching FCM registration token failed", task.getException());
                        return;
                    }

                    // Get new FCM registration token
                    String token = task.getResult();
                    Log.d("FIREBASE_token",token);
                    tok = token;
                    // Log and toast
                });
    }

    public void login(String root){
        Log.w("login","로그인 하는중");
        try {
            id = login_email.getText().toString();
            pw = SHA256.DoHash("!@"+login_password.getText().toString()+"*%");
            Log.w("앱에서 보낸값",id+", "+pw);
            CustomTask task = new CustomTask();
            String result = task.execute(id, pw, root,tok).get();


            Log.w("받은값",result);
            Log.w("받은값 String", result);
            if(result.contains("SUCCESS")) {
                Toast.makeText( getApplicationContext(), String.format("%s님 환영합니다.", id), Toast.LENGTH_SHORT ).show();
                Intent intent = new Intent( LoginActivity.this, MapsActivity.class );
                intent.putExtra("id", id);
                try {
                    intent.putExtra("title", getIntent().getStringExtra("title"));
                    intent.putExtra("body", getIntent().getStringExtra("body"));
                }catch(Exception ignored){}
                startActivity( intent );
            }
            else {
                Toast.makeText( getApplicationContext(), "ID, PW를 다시 확인해주세요.", Toast.LENGTH_SHORT ).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
