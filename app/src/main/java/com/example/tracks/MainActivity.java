package com.example.tracks;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        bottomNavigationView.setVisibility(View.GONE);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (savedInstanceState == null) {
            if (user == null) {
                gotoLoginFragment();
            } else {
                bottomNavigationView.setVisibility(View.VISIBLE);
                pushFragment(new TrackListFragment());
            }
        }

        setupBottomNavigation();
    }

    private void setupBottomNavigation() {

        bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = null;

            if (item.getItemId() == R.id.action_home) {
                selectedFragment = new TrackListFragment();
            }
            else if (item.getItemId() == R.id.action_add) {
                selectedFragment = new AddTrackFragment();
            }
            else if (item.getItemId() == R.id.action_profile) {
                selectedFragment = new ProfileFragment();
            }
            else if (item.getItemId() == R.id.action_signout) {

                FirebaseAuth.getInstance().signOut();
                bottomNavigationView.setVisibility(View.GONE);
                gotoLoginFragment();
                return true;
            }

            if (selectedFragment != null) {
                pushFragment(selectedFragment);
            }

            return true;
        });
    }

    public void pushFragment(Fragment fragment) {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, fragment);
        ft.commit();
    }

    private void gotoLoginFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.frameLayout, new LoginFragment());
        ft.commit();
    }

    public BottomNavigationView getBottomNavigationView() {
        return bottomNavigationView;
    }
}