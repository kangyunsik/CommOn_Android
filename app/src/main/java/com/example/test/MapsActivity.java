
package com.example.test;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.snackbar.Snackbar;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MapsActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        ActivityCompat.OnRequestPermissionsResultCallback,
        GoogleMap.OnInfoWindowClickListener {

    public static final String server_ip = "";

    private long pressedTime;
    private String title;
    private String body;
    private String invited_users;
    public static String Auth_token;

    public static JsonParsingService jpService;

    public static GoogleMap mMap;
    private Marker currentMarker;
    private Marker selectedMarker;
    static protected ArrayList<Marker> friendMarker;
    static protected ArrayList<FriendsInfo> friendsInfoList;
    static protected ArrayList<Marker> landmarkMarker;
    static protected ArrayList<Marker> placeMarker;

    private static final String TAG = "GOOGLE_MAP_TAG";
    private static final int ALARM_POPUP_CODE = 202;
    private static final int OPEN_SETTING_CODE = 501;
    private static final int FRIENDS_LIST_CODE = 1001;
    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PLACE_CODE = 3001;
    private static final int LANDMARK_CODE = 4001;
    private static final int UPDATE_INTERVAL_MS = 5000;  // 5초
    private static final int FASTEST_UPDATE_INTERVAL_MS = 3000; // 3초

    // onRequestPermissionsResult 에서 수신된 결과에서 ActivityCompat.requestPermissions 를 사용한 퍼미션 요청을 구별하기 위해 사용됩니다.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // 앱을 실행하기 위해 필요한 퍼미션을 정의합니다.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // 외부 저장소


    Location mCurrentLocation;
    LatLng currentPosition;

    public static String id;
    String result;
    String fri_list = null;
    String sender = null;
    private FusedLocationProviderClient mFusedLocationClient;
    private LocationRequest locationRequest;
    private Location location;

    private ProgressBar progressBar;

    private View mLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_maps);

        progressBar = findViewById(R.id.progressBar);
        progressBar.getIndeterminateDrawable().setColorFilter(
                getResources().getColor(R.color.colorPrimary),
                android.graphics.PorterDuff.Mode.SRC_IN
        );

        pressedTime = 0;

        friendMarker = new ArrayList<>();
        landmarkMarker = new ArrayList<>();
        placeMarker = new ArrayList<>();
        jpService = new JsonParsingService();
        //find_way_button.findViewById(R.id.find_way_button);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");
        result = intent.getStringExtra("result");
        try {
            title = intent.getStringExtra("title");
            body = intent.getStringExtra("body");
        } catch (Exception e) {
            Log.d("진입","알림 없이 접근");
        }

        mLayout = findViewById(R.id.layout_main);

        locationRequest = new LocationRequest()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(UPDATE_INTERVAL_MS)
                .setFastestInterval(FASTEST_UPDATE_INTERVAL_MS);


        LocationSettingsRequest.Builder builder =
                new LocationSettingsRequest.Builder();

        builder.addLocationRequest(locationRequest);


        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null)
            mapFragment.getMapAsync(this);

    }

    @Override
    public void onBackPressed() {
        if (pressedTime == 0) {
            Toast.makeText(MapsActivity.this, " 한 번 더 누르면 로그아웃합니다.", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        } else {
            long seconds = (System.currentTimeMillis() - pressedTime);
            if (seconds > 2000) {
                Toast.makeText(MapsActivity.this, " 한 번 더 누르면 로그아웃합니다.", Toast.LENGTH_SHORT).show();
                pressedTime = 0;
            } else {
                finish(); // app 종료 시키기
            }
        }
    }

    @SuppressLint("NonConstantResourceId")
    public void onClick(View view) {
        Intent intent;

        switch (view.getId()) {
            case R.id.plusMap:
                mMap.moveCamera(CameraUpdateFactory.zoomIn());
                break;
            case R.id.minusMap:
                mMap.moveCamera(CameraUpdateFactory.zoomOut());
                break;
            case R.id.friends_button:
                if (progressBar.getVisibility() == View.VISIBLE) {
                    Toast.makeText(getApplicationContext(), "잠시 뒤 시도해주세요.", Toast.LENGTH_SHORT).show();
                    break;
                }
                intent = new Intent(MapsActivity.this, FriendslistActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, FRIENDS_LIST_CODE);
                break;
            case R.id.reveal_button:
                intent = new Intent(MapsActivity.this, OpenSettingActivity.class);
                intent.putExtra("id", id);
                startActivityForResult(intent, OPEN_SETTING_CODE);
                break;
            case R.id.logout_button:
                finish();
                break;
        }
    }


    @Override
    public void onMapReady(final @NotNull GoogleMap googleMap) {
        Log.d(TAG, "onMapReady :");

        mMap = googleMap;
        mMap.setOnInfoWindowClickListener(this);
        //런타임 퍼미션 요청 대화상자나 GPS 활성 요청 대화상자 보이기전에
        //지도의 초기위치를 서울로 이동
        //setDefaultLocation();

        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)


            startLocationUpdates(); // 3. 위치 업데이트 시작


        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Snackbar.make(mLayout, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.",
                        Snackbar.LENGTH_INDEFINITE).setAction("확인", view -> {
                            // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult 에서 수신됩니다.
                            ActivityCompat.requestPermissions(MapsActivity.this, REQUIRED_PERMISSIONS,
                                    PERMISSIONS_REQUEST_CODE);
                        }).show();


            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult 에서 수신됩니다.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // 현재 오동작을 해서 주석처리

        //mMap.animateCamera(CameraUpdateFactory.zoomTo(15));
        mMap.setOnMapClickListener(latLng -> Log.d(TAG, "onMapClick :"));
    }

    LocationCallback locationCallback = new LocationCallback() {
        @Override
        public void onLocationResult(@NotNull LocationResult locationResult) {
            super.onLocationResult(locationResult);

            List<Location> locationList = locationResult.getLocations();

            if (locationList.size() > 0) {
                location = locationList.get(locationList.size() - 1);
                //location = locationList.get(0);

                currentPosition
                        = new LatLng(location.getLatitude(), location.getLongitude());


                String markerTitle = "내 위치";
                String markerSnippet = "위도:" + location.getLatitude()
                        + " 경도:" + location.getLongitude();

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //현재 위치에 마커 생성하고 이동
                setCurrentLocation(location, markerTitle, markerSnippet);

                mCurrentLocation = location;
            }


        }

    };


    private void startLocationUpdates() {

        if (!checkLocationServicesStatus()) {

            Log.d(TAG, "startLocationUpdates : call showDialogForLocationServiceSetting");
            showDialogForLocationServiceSetting();
        } else {

            int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION);


            if (hasFineLocationPermission != PackageManager.PERMISSION_GRANTED ||
                    hasCoarseLocationPermission != PackageManager.PERMISSION_GRANTED) {

                Log.d(TAG, "startLocationUpdates : 퍼미션 안가지고 있음");
                return;
            }


            Log.d(TAG, "startLocationUpdates : call mFusedLocationClient.requestLocationUpdates");

            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

            if (checkPermission())
                mMap.setMyLocationEnabled(true);

        }

    }


    @Override
    protected void onStart() {
        super.onStart();

        Log.d(TAG, "onStart");

        if (checkPermission()) {

            Log.d(TAG, "onStart : call mFusedLocationClient.requestLocationUpdates");
            mFusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

            if (mMap != null)
                mMap.setMyLocationEnabled(true);

        }


    }


    @Override
    protected void onStop() {

        super.onStop();

        if (mFusedLocationClient != null) {

            Log.d(TAG, "onStop : call stopLocationUpdates");
            mFusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public void setCurrentLocation(Location location, String markerTitle, String markerSnippet) {
        LatLng currentLatLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (Auth_token == null) {
            Log.w("에러", "Token 값 == null.");
            finish();
        } else {
            send_location("setmylocation", id, String.valueOf(currentLatLng.latitude), String.valueOf(currentLatLng.longitude));
        }

        if (currentMarker == null) {
            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(false);

            @SuppressLint("UseCompatLoadingForDrawables")
            BitmapDrawable bitmapDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.zzanggu);
            Bitmap b = bitmapDrawable.getBitmap();
            Bitmap smallMarker = Bitmap.createScaledBitmap(b, 140, 140, false);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker));
            currentMarker = mMap.addMarker(markerOptions);

            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLng(currentLatLng);
            mMap.moveCamera(cameraUpdate);
            if (title != null) {
                Intent alarm_intent = new Intent(MapsActivity.this, AlarmActivity.class);
                alarm_intent.putExtra("title", title);
                alarm_intent.putExtra("body", body);
                startActivityForResult(alarm_intent, ALARM_POPUP_CODE);
            }
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 15));
            progressBar.setVisibility(View.INVISIBLE);
        } else {
            currentMarker.setPosition(currentLatLng);
        }
    }


    //여기부터는 런타임 퍼미션 처리을 위한 메소드들
    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    /*
     * ActivityCompat.requestPermissions 를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // 퍼미션을 허용했다면 위치 업데이트를 시작합니다.
                startLocationUpdates();
            } else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // 사용자가 거부만 선택한 경우에는 앱을 다시 실행하여 허용을 선택하면 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", view -> finish()).show();
                } else {
                    // "다시 묻지 않음"을 사용자가 체크하고 거부를 선택한 경우에는 설정(앱 정보)에서 퍼미션을 허용해야 앱을 사용할 수 있습니다.
                    Snackbar.make(mLayout, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("확인", view -> finish()).show();
                }
            }
        }
    }

    //여기부터는 GPS 활성화를 위한 메소드들
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"
                + "위치 설정을 수정하실래요?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("취소", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS 활성화 되있음");
                        needRequest = true;
                        return;
                    }
                }
                break;

            case FRIENDS_LIST_CODE:
                if (resultCode == RESULT_OK) {
                    resetMarker(landmarkMarker);
                    resetMarker(placeMarker);

                    fri_list = data.getStringExtra("fri_list");
                    sender = data.getStringExtra("id");

                    jpService.landmarkParsing(data.getStringExtra("landmark_string"));
                    settingLandmark(jpService.getLandmarkArrayList());

                    Landmark l = jpService.getCenterLandMark();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(l.getLatitude(), l.getLongitude()), 12);
                    mMap.moveCamera(cameraUpdate);
                }
                break;

            case PLACE_CODE:
                if (resultCode == RESULT_OK) {
                    String result = data.getStringExtra("result");
                    String placename = data.getStringExtra("name");

                    String[] parse1 = result.split(",");

                    String name_tmp = parse1[1];
                    String lati_tmp = parse1[2];
                    String long_tmp = parse1[3];

                    CustomTask_Invitation customTask_Invitation = new CustomTask_Invitation();
                    customTask_Invitation.execute(sender, name_tmp, lati_tmp, long_tmp, fri_list);

                    for (Marker p : placeMarker)
                        if (!p.getTitle().contains(placename))
                            p.remove();
                }
                break;

            case LANDMARK_CODE:
                if (resultCode == RESULT_OK) {
                    String selected = data.getStringExtra("selected");
                    Marker marker = selectedMarker;

                    MyUtil myUtil = new MyUtil();
                    double dist = myUtil.getBestDist(marker, landmarkMarker);

                    resetMarker(landmarkMarker);
                    resetMarker(placeMarker);

                    CustomTask_place customTask_place = new CustomTask_place();
                    ArrayList<Place> places = null;
                    try {
                        places = customTask_place.execute(Double.toString(marker.getPosition().latitude), Double.toString(marker.getPosition().longitude), selected, Double.toString(dist)).get();
                    } catch (ExecutionException | InterruptedException e) {
                        e.printStackTrace();
                    }

                    for (Place p : places) {
                        LatLng currentLatLng = new LatLng(p.getLatitude(), p.getLongitude());

                        String markerTitle = p.getName();
                        String markerSnippet = "위도:" + p.getLatitude() + " 경도:" + p.getLongitude();

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(currentLatLng);
                        markerOptions.title("장소 :" + markerTitle + " 평점 :" + p.getRating());
                        if(p.getRating() < 0.1)
                            markerOptions.title("장소 :" + markerTitle + " 평점 :" + "2.5");
                        markerOptions.snippet(markerSnippet);
                        markerOptions.draggable(false);
                        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                        Marker place_marker = (mMap.addMarker(markerOptions));
                        placeMarker.add(place_marker);
                    }
                }
                break;

            case OPEN_SETTING_CODE:
                break;

            case ALARM_POPUP_CODE:
                if (resultCode != RESULT_OK)
                    break;

                String initUser;
                String place_name;
                double latitude;
                double longitude;

                initUser = data.getStringExtra("initUser");
                invited_users = data.getStringExtra("users");
                place_name = data.getStringExtra("place_name");
                latitude = data.getDoubleExtra("lati", 0.0);
                longitude = data.getDoubleExtra("longi", 0.0);

                resetMarker(landmarkMarker);
                resetMarker(placeMarker);

                String markerTitle = "장소 :" + place_name + " 개시자 :" + initUser;
                String markerSnippet = "위도:" + latitude + " 경도:" + longitude;

                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(new LatLng(latitude, longitude));
                markerOptions.title(markerTitle);
                markerOptions.snippet(markerSnippet);
                markerOptions.draggable(false);
                markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));

                Marker place_marker = (mMap.addMarker(markerOptions));
                placeMarker.add(place_marker);

                CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 12);
                mMap.moveCamera(cameraUpdate);

                break;
        }
    }

    private void settingLandmark(ArrayList<Landmark> landmarks) {
        resetMarker(landmarkMarker);

        for (int i = 0; i < landmarks.size(); i++) {
            LatLng currentLatLng = new LatLng(landmarks.get(i).getLatitude(), landmarks.get(i).getLongitude());

            String markerTitle = "Landmark #" + (i + 1);
            String markerSnippet = landmarks.get(i).getTimeList();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(false);
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));

            Marker marker = (mMap.addMarker(markerOptions));
            landmarkMarker.add(marker);
        }
    }

    public void send_location(String root, String id, String latitude, String longitude) {
        String result = "";
        try {
            Log.w("앱에서 보낸값", id + ", " + latitude + ", " + ", " + longitude);
            CustomTask_map task_map = new CustomTask_map();
            result = task_map.execute(id, latitude, longitude, root).get();

            Log.w("받은값", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //원하는 정보 추출 위한 replacement
        friendsInfoList = jpService.locationParsing(result);
        setFriends_location(friendsInfoList);
    }

    private void setFriends_location(List<FriendsInfo> friendsInfoList) {
        if (friendsInfoList == null || friendsInfoList.size() == 0)
            return;

        lb:
        for (int i = 0; i < friendsInfoList.size(); i++) {
            FriendsInfo fi = friendsInfoList.get(i);
            LatLng currentLatLng = new LatLng(fi.getLatitude(), fi.getLongitude());

            String markerTitle = fi.getId();

            for (Marker fmarker : friendMarker) {
                if (fmarker.getTitle().equals(fi.getId())) {
                    fmarker.setPosition(currentLatLng);
                    continue lb;
                }
            }

            String markerSnippet = "위도:" + fi.getLatitude() + " 경도:" + fi.getLongitude();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(currentLatLng);
            markerOptions.title(markerTitle);
            markerOptions.snippet(markerSnippet);
            markerOptions.draggable(false);

            @SuppressLint("UseCompatLoadingForDrawables")
            Bitmap b1 = BitmapFactory.decodeResource(getResources(), getResources().getIdentifier("character"+(i%14+1),"drawable",getPackageName()));
            Bitmap smallMarker1 = Bitmap.createScaledBitmap(b1, 140, 140, true);
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(smallMarker1));

            Marker marker = (mMap.addMarker(markerOptions));
            friendMarker.add(marker);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_option, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.menu1) {
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(location.getLatitude(), location.getLongitude()), 15);
            mMap.moveCamera(cameraUpdate);
            Toast.makeText(getApplicationContext(),
                    "현재 내 위치로 이동합니다.",
                    Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        String markerId = marker.getId();
        if (marker.getTitle().contains("Landmark")) {               // 랜드마크 선택 시
            Intent intent = new Intent(this, Landmark_popup.class);
            intent.putExtra("latitude", marker.getPosition().latitude);
            intent.putExtra("longitude", marker.getPosition().longitude);
            startActivityForResult(intent, LANDMARK_CODE);

            selectedMarker = marker;
        } else if (marker.getTitle().contains("개시자")) {
            Toast.makeText(getApplicationContext(),"초대 받은 친구 : " + invited_users,Toast.LENGTH_LONG).show();
        } else if (marker.getTitle().contains("장소 :")) {          // 플레이스 선택 시
            Intent intent = new Intent(this, Place_popup.class);
            intent.putExtra("id", markerId);
            intent.putExtra("name", marker.getTitle().split("장소 :")[1].split(" 평점 :")[0]);
            intent.putExtra("latitude", marker.getPosition().latitude);
            intent.putExtra("longitude", marker.getPosition().longitude);
            intent.putExtra("rating", marker.getTitle().split("장소 :")[1].split(" 평점 :")[1]);

            startActivityForResult(intent, PLACE_CODE);
        } else {
            for (FriendsInfo fi : friendsInfoList) {
                if (marker.getTitle().contains(fi.getId())) {
                    if (fi.getUpdated() == 0) {
                        Toast.makeText(getApplicationContext(), "해당 사용자 위치는 최신입니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "해당 사용자 위치는 " + fi.getUpdated() + "분 전입니다.", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }
    }

    private void resetMarker(ArrayList<Marker> markers) {
        for (int i = 0; i < markers.size(); i++)
            markers.get(i).remove();
    }
}