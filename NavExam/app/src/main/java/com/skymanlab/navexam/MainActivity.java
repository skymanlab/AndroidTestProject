package com.skymanlab.navexam;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavBackStackEntry;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.NavigationUI;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends AppCompatActivity {

    // constant
    private static final String TAG = MainActivity.class.getSimpleName();

    // instance variable : widget
    private NavHostFragment navHostFragment;
    private BottomNavigationView bottomNavigationView;

    // instance variable
    private NavController navController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        navHostFragment = (NavHostFragment) getSupportFragmentManager().findFragmentById(R.id.A_main_nav_host_fragment);
        bottomNavigationView = findViewById(R.id.A_main_bottom_navigation);

        navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(bottomNavigationView, navController);

    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return NavigationUI.onNavDestinationSelected(item, navController) || super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        NavBackStackEntry navBackStackEntry = navController.getCurrentBackStackEntry();
        NavDestination navDestination = navController.getCurrentDestination();

        Log.d(TAG, "< NavBackStackEntry > 객체는 ? = " + navBackStackEntry);
        Log.d(TAG, "< NavDestination > 객체는 ? = " + navDestination);
        Log.d(TAG, "< navDestination > getId 결과는 ? = " + navDestination.getId());


        if (navDestination.getId() == R.id.navGraphHomeFragment) {

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.A_main_Alert_finish_title)
                    .setMessage(R.string.A_main_Alert_finish_message)
                    .setPositiveButton(R.string.A_main_Alert_finish_Btn_positive, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setNegativeButton(R.string.A_main_Alert_finish_Btn_negative, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    })
                    .show();
        } else {
            navController.popBackStack();
        }


    }
}