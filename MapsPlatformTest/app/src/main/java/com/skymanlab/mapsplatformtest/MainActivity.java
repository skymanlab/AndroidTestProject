package com.skymanlab.mapsplatformtest;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.button.MaterialButton;

public class MainActivity extends AppCompatActivity {

    // widget
    private MaterialButton btnPlaceAutocompleteActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initWidget();

    }

    private void initWidget() {

        btnPlaceAutocompleteActivity = (MaterialButton) findViewById(R.id.btn_place_autocomplete_activity_move);
        btnPlaceAutocompleteActivity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(getApplicationContext(), PlaceAutocompleteActivity.class);
                startActivity(intent);

            }
        });
    }

}