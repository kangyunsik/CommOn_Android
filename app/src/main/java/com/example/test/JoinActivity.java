package com.example.test;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class JoinActivity extends AppCompatActivity {
    private EditText join_name, join_email, join_password, join_pwch;
    private Button join_button, delete_button;
    private static Context context;

    String id, pw, name;
    String input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_join );


        join_name = findViewById(R.id.join_name);
        join_email = findViewById( R.id.join_email );
        join_password = findViewById( R.id.join_password );
        join_pwch = findViewById(R.id.join_pwck);

        join_button = findViewById( R.id.join_button );
        delete_button = findViewById(R.id.delete);

        this.context = getApplicationContext();

        join_button.setOnClickListener( new View.OnClickListener() {    // 아이디 비밀번호 서버 DB로 최종으로 보내기
            @Override
            public void onClick(View view) {
                if(join_password.getText().toString().equals(join_pwch.getText().toString())) {
                    String root = "join";
                    join(root);
                }
                else if(!(join_password.getText().toString().equals(join_pwch.getText().toString()))){
                    Toast.makeText( context, String.format("비밀번호 확인이 일치하지 않습니다."), Toast.LENGTH_SHORT ).show();
                }
                else {
                    Toast.makeText( context, String.format("ID 중복입니다."), Toast.LENGTH_SHORT ).show();
                }


            }
        });


        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }
    public void join(String root){
        Log.w("join","회원가입 하는중");
        try {
            String Result_root = root;

            id = join_email.getText().toString();
            pw = SHA256.DoHash(join_password.getText().toString());
            name = join_name.getText().toString();

            if(id.length() < 4 || join_password.getText().toString().length() < 4){
                Toast.makeText(context,"아이디와 비밀번호는 4자리 이상이어야 합니다.",Toast.LENGTH_SHORT).show();
                return;
            }

            Log.w("앱에서 보낸값",id+", "+pw);
            CustomTask task = new CustomTask();
            String result = task.execute(id, pw, Result_root).get();


            Log.w("받은값",result);
            //Log.w("받은값 String",result.toString());
            if(result.contains("OK")) {   //FAIL일 경우 DB에 아이디 비번이 없는 것이므로 가입해야함. 가입은 서버에서 DB에 넣어줌.
                Toast.makeText(context, String.format("%s님 회원가입 완료되었습니다.", name), Toast.LENGTH_SHORT ).show();
                finish();
            }
            else {
                Toast.makeText( context, String.format("ID, PW를 다시 확인해주세요."), Toast.LENGTH_SHORT ).show();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

