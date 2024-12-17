package com.example.pawpal_18nov;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;

public class Make_Profile extends AppCompatActivity {

    // Declare UI components
    private EditText editPetName, editSpecies, editGender, editBirth, editWeight, editHabitat, editSterilize;
    private Button btnSaveProfile;
    private DatabaseReference profileRef;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_profile);

        // Initialize UI components
        editPetName = findViewById(R.id.edit_petName);
        editSpecies = findViewById(R.id.edit_Species);
        editGender = findViewById(R.id.edit_Gender);
        editBirth = findViewById(R.id.edit_Birth);
        editWeight = findViewById(R.id.edit_Weight);
        editHabitat = findViewById(R.id.edit_Habitat);
        editSterilize = findViewById(R.id.edit_Sterilize);
        btnSaveProfile = findViewById(R.id.btn_save_profile);

        // Initialize Firebase Database reference to "Pets" node
        profileRef = FirebaseDatabase.getInstance().getReference("Pets");

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Set OnClickListener for Save Profile button
        btnSaveProfile.setOnClickListener(v -> saveProfile());

        // Add DatePicker to Birth field
        editBirth.setOnClickListener(v -> showDatePickerDialog());

        // Add dropdown menus to Gender, Habitat, and Sterilize fields
        setupDropdown(editGender, R.menu.gender_menu);
        setupDropdown(editHabitat, R.menu.habitat_menu);
        setupDropdown(editSterilize, R.menu.sterilize_menu);
    }

    private void saveProfile() {
        // Get profile data from EditText fields
        String petName = editPetName.getText().toString();
        String species = editSpecies.getText().toString();
        String gender = editGender.getText().toString();
        String birth = editBirth.getText().toString();
        String weight = editWeight.getText().toString();
        String habitat = editHabitat.getText().toString();
        String sterilize = editSterilize.getText().toString();

        // Validate the input fields
        if (TextUtils.isEmpty(petName) || TextUtils.isEmpty(species) || TextUtils.isEmpty(gender) ||
                TextUtils.isEmpty(birth) || TextUtils.isEmpty(weight) || TextUtils.isEmpty(habitat) || TextUtils.isEmpty(sterilize)) {
            Toast.makeText(Make_Profile.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
            return;
        }

        // Get the current authenticated user
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String uid = currentUser.getUid(); // Get the UID of the authenticated user

            // Create a new PetProfile object with the collected data
            PetProfile profile = new PetProfile(petName, species, gender, birth, weight, habitat, sterilize);

            // Save the profile data to Firebase Realtime Database using the UID as the key
            profileRef.child(uid).setValue(profile).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(Make_Profile.this, "Profile saved successfully", Toast.LENGTH_SHORT).show();

                    // Redirect to Home_Page or another activity and pass the profile ID
                    Intent intent = new Intent(Make_Profile.this, Home_Page.class);
                    intent.putExtra("profileId", uid); // Pass the UID as the profile ID
                    startActivity(intent);
                    finish(); // Close the Make_Profile activity
                } else {
                    Toast.makeText(Make_Profile.this, "Error saving profile", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            Toast.makeText(Make_Profile.this, "User not authenticated", Toast.LENGTH_SHORT).show();
        }
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                Make_Profile.this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    String date = selectedDay + "/" + (selectedMonth + 1) + "/" + selectedYear;
                    editBirth.setText(date);
                },
                year,
                month,
                day
        );

        datePickerDialog.show();
    }

    private void setupDropdown(EditText editText, int menuResId) {
        editText.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(Make_Profile.this, editText);
            popupMenu.getMenuInflater().inflate(menuResId, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                editText.setText(item.getTitle());
                return true;
            });
            popupMenu.show();
        });
    }
}
