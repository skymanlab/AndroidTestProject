package com.skymanlab.mapsplatformtest.manager;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.skymanlab.mapsplatformtest.R;

import java.util.Arrays;
import java.util.List;

public class PlaceAutocompleteManager {

    // constance
    private static final String META_DATA_NULL = "메타 데이터가 없습니다.";
    private static final String NOT_INIT_PLACES_API = "Place API 초기화에 실패하였습니다.";
    private static String COUNTRY = "KR";

    // variable
    private ActivityResultLauncher mStartForResult;
    private PlacesClient placesClient;

    public PlacesClient getPlacesClient() {
        return placesClient;
    }

    /**
     * init Places SDK
     *
     * @return init result
     */
    public boolean initPlacesAPI(@NonNull AppCompatActivity appCompatActivity) {

        // AndroidManifest.xml 에 정의되어 있는 meta-data 가져오기
        try {

            ApplicationInfo info = appCompatActivity.getPackageManager().getApplicationInfo(
                    appCompatActivity.getPackageName(),
                    PackageManager.GET_META_DATA
            );

            if (info.metaData != null) {

                // meta-data 에 저장되어 있는 Places API Key 가져오기
                String apiKey = info.metaData.getString("com.google.android.geo.API_KEY");

                // initialize Places SDK
                Places.initialize(appCompatActivity.getApplicationContext(), apiKey);

                // create new PlacesClient object
                placesClient = Places.createClient(appCompatActivity);

                Log.i(TagManager.TAG_RESULT, "Places initialize 결과 : " + Places.isInitialized());

                return true;

            } else {

                Log.e(TagManager.TAG_ERROR, META_DATA_NULL);
                return false;

            }

        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return false;
    }

    public ActivityResultLauncher setActivityResultLauncherForOption2(@NonNull AppCompatActivity appCompatActivity) {

        mStartForResult = appCompatActivity.registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {

                        if (result.getResultCode() == Activity.RESULT_OK) {

                            Place place = Autocomplete.getPlaceFromIntent(result.getData());

                            Log.println(Log.INFO, TagManager.TAG_RESULT, toStringFromPlace(place));

                        }

                    }
                }
        );

        return mStartForResult;
    }


    public void executePlaceAutocompleteOption1(@NonNull AppCompatActivity appCompatActivity) {

        //
        AutocompleteSupportFragment autocompleteSupportFragment =
                (AutocompleteSupportFragment) appCompatActivity
                        .getSupportFragmentManager()
                        .findFragmentById(R.id.autocomplete_support_fragment);

        // set Place Fields
        List<Place.Field> fields = setPlaceFields();

        autocompleteSupportFragment.setPlaceFields(fields);

        // response listener
        autocompleteSupportFragment.setOnPlaceSelectedListener(
                new PlaceSelectionListener() {
                    @Override
                    public void onError(@NonNull Status status) {

                    }

                    @Override
                    public void onPlaceSelected(@NonNull Place place) {

                        Log.println(Log.INFO, TagManager.TAG_RESULT, toStringFromPlace(place));

                    }
                }
        );
    }


    public void executePlaceAutocompleteOption2(@NonNull Context context) {

        if (mStartForResult == null)
            return;

        // set Place Fields
        List<Place.Field> fields = setPlaceFields();

        // 2번째 방법은 Intent 객체를 만들 때
        //
        Intent intent = new Autocomplete.IntentBuilder(
                AutocompleteActivityMode.FULLSCREEN,
                fields)
                .setCountry(COUNTRY)
                .build(context);
        mStartForResult.launch(intent);
    }


    private List<Place.Field> setPlaceFields() {

        List<Place.Field> fields = Arrays.asList(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.LAT_LNG);

        return fields;
    }

    private String toStringFromPlace(Place place) {

        // 장소표현
        StringBuilder placeInfo = new StringBuilder();
        placeInfo.append("장소 : ");
        placeInfo.append(place.getName());
        placeInfo.append("\n");
        placeInfo.append("ID : ");
        placeInfo.append(place.getId());
        placeInfo.append("\n");
        placeInfo.append("주소 : ");
        placeInfo.append(place.getAddress());
        placeInfo.append("\n");
        placeInfo.append("위도 : ");
        placeInfo.append(place.getLatLng().latitude);
        placeInfo.append("\n");
        placeInfo.append("경도 : ");
        placeInfo.append(place.getLatLng().longitude);
        placeInfo.append("\n");

        return placeInfo.toString();
    }
}
