package com.example.test;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

public class Landmark_popup extends Activity {
    String selected = "restaurant";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_landmark_popup);

        final RadioGroup rg = (RadioGroup)findViewById(R.id.radiogroup_choose_place);
        TextView explain = (TextView) findViewById(R.id.explain_landmark);

        rg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                String result;
                if(checkedId == R.id.radio_restaurant1){
                    explain.setText("해당 랜드마크를 기준으로 식당을 찾으시겠습니까?");
                    selected = "restaurant";
                }
                else if(checkedId == R.id.radio_cafe2){
                    explain.setText("해당 랜드마크를 기준으로 카페를 찾으시겠습니까?");
                    selected = "cafe";
                }
                else if(checkedId == R.id.radio_station3){
                    explain.setText("해당 랜드마크를 기준으로 역을 찾으시겠습니까?");
                    selected = "역";
                }
            }
        });

    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.place_ok:
                Intent intent = new Intent();
                intent.putExtra("selected",selected);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.place_cancel:
                finish();
                break;

        }
    }
}

