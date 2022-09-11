package com.skymanlab.mapsplatformtest;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import com.google.android.gms.common.api.Status;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.button.MaterialButton;
import com.skymanlab.mapsplatformtest.manager.MapsManager;
import com.skymanlab.mapsplatformtest.manager.PlaceAutocompleteManager;
import com.skymanlab.mapsplatformtest.manager.TagManager;

import java.util.Arrays;
import java.util.List;

public class PlaceAutocompleteActivity extends AppCompatActivity {

    // widget
    private MaterialButton btnPlaceAutocompleteOption2;

    // variable
    private PlaceAutocompleteManager placeAutocompleteManager;
    private MapsManager mapsManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_autocomplete);

        initWidget();
        initPlaceAutocomplete();
        initMapsManager();


    }

    /**
     * init widget
     */
    private void initWidget() {

        btnPlaceAutocompleteOption2 = (MaterialButton) findViewById(R.id.btn_place_autocomplete_option2);
        btnPlaceAutocompleteOption2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                if (placeAutocompleteManager != null) {
//                    placeAutocompleteManager.executePlaceAutocompleteOption2(getApplicationContext());
//                }

                if (placeAutocompleteManager !=null && mapsManager != null) {
                    mapsManager.showCurrentPlace(placeAutocompleteManager.getPlacesClient(), getApplicationContext(), PlaceAutocompleteActivity.this);

                }
            }
        });

    }

    /**
     * init Place Autocomplete
     */
    private void initPlaceAutocomplete() {

        placeAutocompleteManager = new PlaceAutocompleteManager();

        if (placeAutocompleteManager.initPlacesAPI(this)) {

            placeAutocompleteManager.setActivityResultLauncherForOption2(this);
            placeAutocompleteManager.executePlaceAutocompleteOption1(this);

        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {


        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * init MapsManager
     */
    private void initMapsManager() {

        mapsManager = new MapsManager();


        mapsManager.responseAccessCoarseLocationPermissionOption2Result(this);
        mapsManager.responseAccessFineLocationPermissionOption2Result(this);
        mapsManager.getAccessCoarseLocationPermissionOption2(getApplicationContext(), this);
        mapsManager.getAccessFineLocationPermissionOption2(getApplicationContext(), this);

        mapsManager.initFusedLocationProviderClient(this);
        mapsManager.initGoogleMap(this);


    }
}