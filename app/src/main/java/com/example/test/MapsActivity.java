
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
    private static final int UPDATE_INTERVAL_MS = 5000;  // 5???
    private static final int FASTEST_UPDATE_INTERVAL_MS = 3000; // 3???

    // onRequestPermissionsResult ?????? ????????? ???????????? ActivityCompat.requestPermissions ??? ????????? ????????? ????????? ???????????? ?????? ???????????????.
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    boolean needRequest = false;

    // ?????? ???????????? ?????? ????????? ???????????? ???????????????.
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};  // ?????? ?????????


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
            Log.d("??????","?????? ?????? ??????");
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
            Toast.makeText(MapsActivity.this, " ??? ??? ??? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
            pressedTime = System.currentTimeMillis();
        } else {
            long seconds = (System.currentTimeMillis() - pressedTime);
            if (seconds > 2000) {
                Toast.makeText(MapsActivity.this, " ??? ??? ??? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                pressedTime = 0;
            } else {
                finish(); // app ?????? ?????????
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
                    Toast.makeText(getApplicationContext(), "?????? ??? ??????????????????.", Toast.LENGTH_SHORT).show();
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
        //????????? ????????? ?????? ??????????????? GPS ?????? ?????? ???????????? ???????????????
        //????????? ??????????????? ????????? ??????
        //setDefaultLocation();

        //????????? ????????? ??????
        // 1. ?????? ???????????? ????????? ????????? ???????????????.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. ?????? ???????????? ????????? ?????????
            // ( ??????????????? 6.0 ?????? ????????? ????????? ???????????? ???????????? ????????? ?????? ????????? ?????? ???????????????.)


            startLocationUpdates(); // 3. ?????? ???????????? ??????


        } else {  //2. ????????? ????????? ????????? ?????? ????????? ????????? ????????? ???????????????. 2?????? ??????(3-1, 4-1)??? ????????????.

            // 3-1. ???????????? ????????? ????????? ??? ?????? ?????? ????????????
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. ????????? ???????????? ?????? ?????????????????? ???????????? ????????? ????????? ???????????? ????????? ????????????.
                Snackbar.make(mLayout, "??? ?????? ??????????????? ?????? ?????? ????????? ???????????????.",
                        Snackbar.LENGTH_INDEFINITE).setAction("??????", view -> {
                            // 3-3. ??????????????? ????????? ????????? ?????????. ?????? ????????? onRequestPermissionResult ?????? ???????????????.
                            ActivityCompat.requestPermissions(MapsActivity.this, REQUIRED_PERMISSIONS,
                                    PERMISSIONS_REQUEST_CODE);
                        }).show();


            } else {
                // 4-1. ???????????? ????????? ????????? ??? ?????? ?????? ???????????? ????????? ????????? ?????? ?????????.
                // ?????? ????????? onRequestPermissionResult ?????? ???????????????.
                ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        // ?????? ???????????? ?????? ????????????

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


                String markerTitle = "??? ??????";
                String markerSnippet = "??????:" + location.getLatitude()
                        + " ??????:" + location.getLongitude();

                Log.d(TAG, "onLocationResult : " + markerSnippet);


                //?????? ????????? ?????? ???????????? ??????
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

                Log.d(TAG, "startLocationUpdates : ????????? ???????????? ??????");
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
            Log.w("??????", "Token ??? == null.");
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


    //??????????????? ????????? ????????? ????????? ?????? ????????????
    private boolean checkPermission() {
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION);

        return hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED;
    }

    /*
     * ActivityCompat.requestPermissions ??? ????????? ????????? ????????? ????????? ???????????? ??????????????????.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if (permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // ?????? ????????? PERMISSIONS_REQUEST_CODE ??????, ????????? ????????? ???????????? ??????????????????
            boolean check_result = true;

            // ?????? ???????????? ??????????????? ???????????????.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if (check_result) {
                // ???????????? ??????????????? ?????? ??????????????? ???????????????.
                startLocationUpdates();
            } else {
                // ????????? ???????????? ????????? ?????? ????????? ??? ?????? ????????? ??????????????? ?????? ???????????????.2 ?????? ????????? ????????????.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    // ???????????? ????????? ????????? ???????????? ?????? ?????? ???????????? ????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ?????? ?????? ???????????? ???????????? ??????????????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", view -> finish()).show();
                } else {
                    // "?????? ?????? ??????"??? ???????????? ???????????? ????????? ????????? ???????????? ??????(??? ??????)?????? ???????????? ???????????? ?????? ????????? ??? ????????????.
                    Snackbar.make(mLayout, "???????????? ?????????????????????. ??????(??? ??????)?????? ???????????? ???????????? ?????????. ",
                            Snackbar.LENGTH_INDEFINITE).setAction("??????", view -> finish()).show();
                }
            }
        }
    }

    //??????????????? GPS ???????????? ?????? ????????????
    private void showDialogForLocationServiceSetting() {

        AlertDialog.Builder builder = new AlertDialog.Builder(MapsActivity.this);
        builder.setTitle("?????? ????????? ????????????");
        builder.setMessage("?????? ???????????? ???????????? ?????? ???????????? ???????????????.\n"
                + "?????? ????????? ???????????????????");
        builder.setCancelable(true);
        builder.setPositiveButton("??????", (dialog, id) -> {
            Intent callGPSSettingIntent
                    = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
        });
        builder.setNegativeButton("??????", (dialog, id) -> dialog.cancel());
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //???????????? GPS ?????? ???????????? ??????
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d(TAG, "onActivityResult : GPS ????????? ?????????");
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
                        String markerSnippet = "??????:" + p.getLatitude() + " ??????:" + p.getLongitude();

                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.position(currentLatLng);
                        markerOptions.title("?????? :" + markerTitle + " ?????? :" + p.getRating());
                        if(p.getRating() < 0.1)
                            markerOptions.title("?????? :" + markerTitle + " ?????? :" + "2.5");
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

                String markerTitle = "?????? :" + place_name + " ????????? :" + initUser;
                String markerSnippet = "??????:" + latitude + " ??????:" + longitude;

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
            Log.w("????????? ?????????", id + ", " + latitude + ", " + ", " + longitude);
            CustomTask_map task_map = new CustomTask_map();
            result = task_map.execute(id, latitude, longitude, root).get();

            Log.w("?????????", result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //????????? ?????? ?????? ?????? replacement
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

            String markerSnippet = "??????:" + fi.getLatitude() + " ??????:" + fi.getLongitude();

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
                    "?????? ??? ????????? ???????????????.",
                    Toast.LENGTH_LONG).show();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {
        String markerId = marker.getId();
        if (marker.getTitle().contains("Landmark")) {               // ???????????? ?????? ???
            Intent intent = new Intent(this, Landmark_popup.class);
            intent.putExtra("latitude", marker.getPosition().latitude);
            intent.putExtra("longitude", marker.getPosition().longitude);
            startActivityForResult(intent, LANDMARK_CODE);

            selectedMarker = marker;
        } else if (marker.getTitle().contains("?????????")) {
            Toast.makeText(getApplicationContext(),"?????? ?????? ?????? : " + invited_users,Toast.LENGTH_LONG).show();
        } else if (marker.getTitle().contains("?????? :")) {          // ???????????? ?????? ???
            Intent intent = new Intent(this, Place_popup.class);
            intent.putExtra("id", markerId);
            intent.putExtra("name", marker.getTitle().split("?????? :")[1].split(" ?????? :")[0]);
            intent.putExtra("latitude", marker.getPosition().latitude);
            intent.putExtra("longitude", marker.getPosition().longitude);
            intent.putExtra("rating", marker.getTitle().split("?????? :")[1].split(" ?????? :")[1]);

            startActivityForResult(intent, PLACE_CODE);
        } else {
            for (FriendsInfo fi : friendsInfoList) {
                if (marker.getTitle().contains(fi.getId())) {
                    if (fi.getUpdated() == 0) {
                        Toast.makeText(getApplicationContext(), "?????? ????????? ????????? ???????????????.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "?????? ????????? ????????? " + fi.getUpdated() + "??? ????????????.", Toast.LENGTH_SHORT).show();
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