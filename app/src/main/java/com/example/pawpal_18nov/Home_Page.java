package com.example.pawpal_18nov;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.database.FirebaseDatabase;

public class Home_Page extends AppCompatActivity {
    private BottomNavigationView bnView;
    FirebaseDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the title for the activity
        //this.setTitle("Home Page");
        if (getSupportActionBar() != null)
            getSupportActionBar().hide();
        EdgeToEdge.enable(this);

        // Set the content view
        setContentView(R.layout.activity_home_page);

        // Adjust for window insets (Edge-to-Edge UI)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize BottomNavigationView
        bnView = findViewById(R.id.bnView);

        // Set up navigation item selection listener
        bnView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.nav_profile) {
                    // Create ProfileFragment and pass data via Bundle
                    ProfileFragment profileFragment = new ProfileFragment();
                    Bundle bundle = new Bundle();
                    bundle.putString("petName", "Buddy");
                    bundle.putString("species", "Dog");
                    bundle.putString("gender", "Male");
                    bundle.putString("birth", "01/01/2020");
                    bundle.putString("weight", "15kg");
                    bundle.putString("habitat", "Home");
                    bundle.putString("sterilize", "Yes");

                    // Pass the bundle to the fragment
                    profileFragment.setArguments(bundle);

                    // Load the ProfileFragment
                    loadFrag(profileFragment, false);
                } else if (item.getItemId() == R.id.nav_task) {
                    loadFrag(new ActicityFragment(), false);
                } else if (item.getItemId() == R.id.nav_expense) {
                    loadFrag(new ExpenseFragment(), false);
                } else if (item.getItemId() == R.id.nav_vet) {
                    loadFrag(new VetInfoFragment(), false);
                }
                return true;
            }
        });

        // Set default selected item and load the corresponding fragment
        if (savedInstanceState == null) {
            bnView.setSelectedItemId(R.id.nav_profile); // Default fragment
        }
    }

    // Method to load a fragment
    public void loadFrag(Fragment fragment, boolean addToBackStack) {
        database = FirebaseDatabase.getInstance();

        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();

        ft.replace(R.id.frame_container, fragment); // `R.id.container` is the FrameLayout in `activity_home_page.xml`

        if (addToBackStack) {
            ft.addToBackStack(null);
        }

        ft.commit();
    }
}


