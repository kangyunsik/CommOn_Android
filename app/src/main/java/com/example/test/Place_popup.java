package com.example.test;


import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import org.w3c.dom.Text;

public class Place_popup extends Activity {
    String result;
    String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_place_popup);

        Intent intent = getIntent();
        String id = intent.getExtras().getString("id");
        name = intent.getExtras().getString("name");
        Double latitude = intent.getExtras().getDouble("latitude");
        Double longitude = intent.getExtras().getDouble("longitude");
        String rating = intent.getExtras().getString("rating");
        Log.w("place_name : ", name);
        Log.w("place_latlng : ", String.valueOf(latitude + " " + longitude));
        Log.w("place_rating : ", rating);
        Log.w("line : ","--------------------------------");

        Uri uri= Uri.parse(String.format("geo:%f,%f?q=%s", latitude, longitude,name));
        Intent in= new Intent(Intent.ACTION_VIEW,uri);
        startActivity(in);

        TextView place_name = (TextView)findViewById(R.id.place_name);
        place_name.setText(name);
        Log.w("place_name : ", (String) place_name.getText());

        TextView place_latlng = (TextView)findViewById(R.id.place_latlng);
        place_latlng.setText("위도 : " + latitude + ",경도 : " + longitude);
        Log.w("place_latlng : ", (String) place_latlng.getText());

        TextView place_rating = (TextView)findViewById(R.id.place_rating);
        place_rating.setText("평점 : " + rating);
        Log.w("place_rating : ", (String) place_rating.getText());

        result += id + "," + name + "," + latitude.toString() + "," + longitude.toString();
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.place_ok:
                Intent intent = new Intent();
                intent.putExtra("result",result);
                intent.putExtra("name",name);
                setResult(RESULT_OK, intent);
                finish();
                break;
            case R.id.place_cancel:
                finish();
                break;
        }
    }
}
