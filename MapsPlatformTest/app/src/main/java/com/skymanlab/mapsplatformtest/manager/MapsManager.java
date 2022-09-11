package com.skymanlab.mapsplatformtest.manager;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.util.Log;
import android.view.View;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.PlaceLikelihood;
import com.google.android.libraries.places.api.net.FindCurrentPlaceRequest;
import com.google.android.libraries.places.api.net.FindCurrentPlaceResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.skymanlab.mapsplatformtest.R;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class MapsManager {

    // Permission
    private static final String ACCESS_FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String ACCESS_COARSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;

    // constance
    private static final int PERMISSION_REQUEST_CODE = 10000;
    private static final float DEFAULT_ZOOM = 15;
    private static final int M_MAX_ENTRIES = 10;

    // variable
    private boolean fineLocationGranted;
    private boolean coarseLocationGranted;

    // instance variable
    private ActivityResultLauncher<String[]> mRequestAccessCoarseLocationPermission;
    private ActivityResultLauncher<String[]> mRequestAccessFineLocationPermission;
    private OnMapReadyCallback onMapReadyCallback;
    private SupportMapFragment supportMapFragment;
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location lastKnownLocation;
    private LatLng defaultLocation;


    /**
     * check/request ACCESS_COARSE_LOCATION permission option_1
     * <p>
     * 1. check's method : ContextCompat.checkSelPermission() and ActivityCompat.shouldShowRequestPermissionRationale()
     * 2. request's method : ActivityCompat.requestPermissions()
     *
     * @param context  ContextCompat.checkSelPermission() 의 매개변수
     * @param activity ActivityCompat.shouldShowRequestPermissionRationale() 와 ActivityCompat.requestPermission() 의 매개변수
     * @param view     Snackbar.make()의 매개변수
     */
    public void getAccessCoarseLocationPermissionOption1(Context context, Activity activity, View view) {

        coarseLocationGranted = false;

        // 1. ACCESS_FINE_LOCATION 권한의 승인/거부 여부 확인
        // 2. 사용자에게 거부된 적이 있는지 확인 -> 사용자에게 필요성 알리고 다시 권한 요청
        // 3. 만약 위의 경우에 해당되지 않을 때 권한 요청하기 -> 권한 요청
        // 참고) else if 의 경우 모든 경우를 다 확인 한다.
        if (ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 이미 권한 승인
            coarseLocationGranted = true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_COARSE_LOCATION)) {
            // 사용자에게 권한 필요성을 설명(Snackbar 사용) -> 권한 요청
        } else {
            // 권한 요청(권한은 하나씩만 요청하기) : ACCESS_COARSE_LOCATION
            ActivityCompat.requestPermissions(activity, new String[]{ACCESS_COARSE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }


    /**
     * check/request ACCESS_FINE_LOCATION permission option_1
     * <p>
     * 1. check's method : ContextCompat.checkSelPermission() and ActivityCompat.shouldShowRequestPermissionRationale()
     * 2. request's method : ActivityCompat.requestPermissions()
     *
     * @param context  ContextCompat.checkSelPermission() 의 매개변수
     * @param activity ActivityCompat.shouldShowRequestPermissionRationale() 와 ActivityCompat.requestPermission() 의 매개변수
     * @param view     Snackbar.make()의 매개변수
     */
    public void getAccessFineLocationPermissionOption1(Context context, Activity activity, View view) {

        fineLocationGranted = false;

        if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // 이미 권한 승인
            fineLocationGranted = true;
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)) {
            // 사용자에게 권한 필요성을 설명(Snackbar 사용) -> 권한 요청
        } else {
            // 권한 요청(권한은 하나씩만 요청하기) : ACCESS_COARSE_LOCATION
            ActivityCompat.requestPermissions(activity, new String[]{ACCESS_FINE_LOCATION}, PERMISSION_REQUEST_CODE);
        }
    }

    /**
     * response ACCESS_COARSE_LOCATION permission option_1's result ( grated/denied )
     *
     * @param grantResult ACCESS_COARSE_LOCATION permission's granted/denied
     */
    public void responseAccessCoarseLocationPermissionOption1Result(int grantResult) {

        // grated = true, denied = false;
        // 권한 요청 결과가 어떻게 되었는지 모르니깐 기본 값인 false, 즉 거부
        coarseLocationGranted = false;

        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            // 권한 요청 결과는 승인되었음
            coarseLocationGranted = true;
        }
    }

    /**
     * response ACCESS_FINE_LOCATION permissiono option_1's result ( grated/denied )
     *
     * @param grantResult ACCESS_FINE_LOCATION permission's granted/defied
     */
    public void responseAccessFineLocationPermissionOption1Result(int grantResult) {
        fineLocationGranted = false;
        if (grantResult == PackageManager.PERMISSION_GRANTED) {
            fineLocationGranted = true;
        }
    }


    /**
     * check/request ACCESS_COARSE_LOCATION permission option_2
     * <p>
     * 1. check's method : ContextCompat.checkSelPermission() and ActivityCompat.shouldShowRequestPermissionRationale()
     * 2. request's method : ActivityResultLauncher instance's launcher()
     *
     * @param context
     * @param activity
     */
    public void getAccessCoarseLocationPermissionOption2(Context context, Activity activity) {

        if (mRequestAccessCoarseLocationPermission != null) {

            if (ContextCompat.checkSelfPermission(context, ACCESS_COARSE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // 이미 권한 승인
                coarseLocationGranted = true;
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_COARSE_LOCATION)) {
                // 사용자에게 권한 필요성을 설명(Snackbar 사용) -> 권한 요청
            } else {
                mRequestAccessCoarseLocationPermission.launch(new String[]{ACCESS_COARSE_LOCATION});
            }
        }
    }

    /**
     * check/request ACCESS_FINE_LOCATION permission option_2
     * <p>
     * 1. check's method : ContextCompat.checkSelPermission() and ActivityCompat.shouldShowRequestPermissionRationale()
     * 2. request's method : ActivityResultLauncher instance's launcher()
     *
     * @param context
     * @param activity
     */
    public void getAccessFineLocationPermissionOption2(Context context, Activity activity) {
        fineLocationGranted = false;

        Log.i(TagManager.TAG_RESULT, "===============> 권한 요청 중");
        if (mRequestAccessFineLocationPermission != null) {

            if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                // 이미 권한 승인
                fineLocationGranted = true;
                Log.i(TagManager.TAG_RESULT, "이미 권한 승인");
            } else if (ActivityCompat.shouldShowRequestPermissionRationale(activity, ACCESS_FINE_LOCATION)) {
                // 사용자에게 권한 필요성을 설명(Snackbar 사용) -> 권한 요청
                Log.i(TagManager.TAG_RESULT, "사용자가 권한 거부 중");
            } else {
                // 권한 요청하기
                mRequestAccessFineLocationPermission.launch(new String[]{ACCESS_FINE_LOCATION});
                Log.i(TagManager.TAG_RESULT, "권한 요청");
            }
        }
    }

    /**
     * @param appCompatActivity
     */
    public void responseAccessCoarseLocationPermissionOption2Result(AppCompatActivity appCompatActivity) {

        coarseLocationGranted = false;

        mRequestAccessCoarseLocationPermission = appCompatActivity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {

                        // 권한 요청의 결과 값 받아옴, 기본 값은 false 로 셋팅
                        Boolean tempCoarseLocationGranted = result.getOrDefault(ACCESS_COARSE_LOCATION, false);

                        if (tempCoarseLocationGranted != null && tempCoarseLocationGranted) {
                            coarseLocationGranted = true;
                            Log.i(TagManager.TAG_RESULT, "coarse 결과로 권한 확인: 승인 ");
                        }

                    }
                });
    }


    /**
     * response ACCESS_FINE_LOCATION permission option_2's granted/denied
     * <p>
     * 주의) 생명주기 중 onCreate()에 위치하기
     *
     * @param appCompatActivity AppCompatActivity.registerForActivityResult() :
     */
    public void responseAccessFineLocationPermissionOption2Result(@NonNull AppCompatActivity appCompatActivity) {

        fineLocationGranted = false;

        mRequestAccessFineLocationPermission = appCompatActivity.registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                new ActivityResultCallback<Map<String, Boolean>>() {
                    @Override
                    public void onActivityResult(Map<String, Boolean> result) {

                        Boolean tempFineLocationGranted = result.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);

                        if (tempFineLocationGranted != null && tempFineLocationGranted) {
                            // 권한 요청에 대한 응답 : 승인
                            fineLocationGranted = true;
                            Log.i(TagManager.TAG_RESULT, "fine 결과로 권한 확인: 승인 ");


                        }
                    }
                });

        //
        updateLocationUI();
        Log.i(TagManager.TAG_RESULT, "객체 생성 완료");
    }


    public void initGoogleMap(AppCompatActivity appCompatActivity) {

        // 중요) 구글 맵이 준비되면 구글 맵을 설정하는 콜백 메소드
        onMapReadyCallback = new OnMapReadyCallback() {
            @Override
            public void onMapReady(GoogleMap map) {

                googleMap = map;
                // Turn on the My Location layer and the related control on the map.
                updateLocationUI();

                // Get the current location of the device and set the position of the map.
                getDeviceLocation(appCompatActivity);

            }
        };

        supportMapFragment = (SupportMapFragment) appCompatActivity
                .getSupportFragmentManager()
                .findFragmentById(R.id.support_map_fragment);

        supportMapFragment.getMapAsync(onMapReadyCallback);

    }


    public void initFusedLocationProviderClient(@NonNull Activity activity) {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);
        defaultLocation = new LatLng(37.5666805, 126.9784147);

    }


    @SuppressLint("MissingPermission")
    private void updateLocationUI() {
        if (googleMap == null)
            return;

        try {
            if (fineLocationGranted) {
                googleMap.setMyLocationEnabled(true);
                googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                Log.i(TagManager.TAG_RESULT, "권한이 승인되어 google map UI를 업데이트 합니다.");
            } else {
                googleMap.setMyLocationEnabled(false);
                googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                Log.i(TagManager.TAG_RESULT, "권한이 거부되어 못합니다.");
            }

        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param appCompatActivity
     */
    @SuppressLint("MissingPermission")
    private void getDeviceLocation(AppCompatActivity appCompatActivity) {

        /*
        Get the best and most recent location of the device, which may be null in rare cases when a location is not available.
         */
        if (fineLocationGranted) {
            Task<Location> locationResult = fusedLocationProviderClient.getLastLocation();
            locationResult.addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    Log.i(TagManager.TAG_RESULT, "OnSuccessListener / Location 객체");
                }
            });
            locationResult.addOnCompleteListener(appCompatActivity, new OnCompleteListener<Location>() {
                @Override
                public void onComplete(@NonNull Task<Location> task) {
                    Log.i(TagManager.TAG_RESULT, "OnCompleteListener / Task<Location> 객체");
                    if (task.isSuccessful()) {
                        Log.i(TagManager.TAG_RESULT, "OnCompleteListener / Task<Location> 객체 / isSuccessful() 확인");

                        // Set the map's camera position to the current
                        lastKnownLocation = task.getResult();

                        if (lastKnownLocation != null) {
                            // lastKnownLocation 의 위도와 경도로 이동, 카메라 줌은 DEFAULT_ZOOM (float)
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                                    new LatLng(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude()),
                                    DEFAULT_ZOOM
                            ));
                            Log.i(TagManager.TAG_RESULT, "ture / lastLocation lat : " + lastKnownLocation.getLatitude() + " , lng : " + lastKnownLocation.getLongitude());
                        } else {
                            // 기본 위치로 이동
                            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(defaultLocation, DEFAULT_ZOOM));
                            googleMap.getUiSettings().setMyLocationButtonEnabled(false);
                            Log.i(TagManager.TAG_RESULT, "false / defaultLocation lat : " + defaultLocation.latitude + " , lng : " + defaultLocation.longitude);
                        }
                    }
                }
            });
        }

    }


    String[] likelyPlaceNames;
    String[] likelyPlaceAddresses;
    List[] likelyPlaceAttributions;
    LatLng[] likelyPlaceLatLngs;

    @SuppressLint("MissingPermission")
    public void showCurrentPlace(PlacesClient placesClient, Context context, Activity activity) {

        if (googleMap == null || placesClient ==null)
            return;

        if (fineLocationGranted) {
            List<Place.Field> placeFields = Arrays.asList(Place.Field.NAME, Place.Field.ADDRESS, Place.Field.LAT_LNG);

            FindCurrentPlaceRequest request = FindCurrentPlaceRequest.newInstance(placeFields);


            Task<FindCurrentPlaceResponse> placeResult =

                    placesClient.findCurrentPlace(request);

            placeResult.addOnCompleteListener(new OnCompleteListener<FindCurrentPlaceResponse>() {
                @Override
                public void onComplete(@NonNull Task<FindCurrentPlaceResponse> task) {

                    if (task.isSuccessful() && task.getResult() != null) {
                        FindCurrentPlaceResponse likelyPlaces = task.getResult();

                        int count;
                        if (likelyPlaces.getPlaceLikelihoods().size() < M_MAX_ENTRIES) {
                            count = likelyPlaces.getPlaceLikelihoods().size();
                        } else {
                            count = M_MAX_ENTRIES;
                        }

                        int i = 0;

                        likelyPlaceNames = new String[count];
                        likelyPlaceAddresses = new String[count];
                        likelyPlaceAttributions = new List[count];
                        likelyPlaceLatLngs = new LatLng[count];

                        for (PlaceLikelihood placeLikelihood : likelyPlaces.getPlaceLikelihoods()) {

                            likelyPlaceNames[i] = placeLikelihood.getPlace().getName();
                            likelyPlaceAddresses[i] = placeLikelihood.getPlace().getAddress();
                            likelyPlaceAttributions[i] = placeLikelihood.getPlace().getAttributions();
                            likelyPlaceLatLngs[i] = placeLikelihood.getPlace().getLatLng();


                            StringBuilder temp = new StringBuilder();
                            temp.append("이름 : ");
                            temp.append(likelyPlaceNames[i]);
                            temp.append(", 주소 : ");
                            temp.append(likelyPlaceAddresses[i]);

                            Log.i(TagManager.TAG_RESULT, temp.toString());

                            i++;
                            if (i > (count-1)) {
                                break;
                            }
                        }


                        Log.i("내용 확인", "좋아하는 장소 개수 : " + i);
                    }else {
                        Log.i(TagManager.TAG_RESULT, "The user did not grant location permission. / 위치권한이 승인되지 않았습니다.");

                        googleMap.addMarker(new MarkerOptions().title("dd").position(defaultLocation).snippet("ddsdfsdfs"));

                        getAccessFineLocationPermissionOption2(context, activity);
                    }
                }
            });


        }
    }

}
