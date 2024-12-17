package com.example.pawpal_18nov;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class ProfileFragment extends Fragment {

    private EditText editPetName, editSpecies, editGender, editBirth, editWeight, editHabitat, editSterilize;
    private Button btnEditProfile, btnDeleteProfile, btnLogOut;
    private DatabaseReference profileRef;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private static final String TAG = "ProfileFragment";

    private boolean isEditing = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize UI elements
        editPetName = rootView.findViewById(R.id.editPetName);
        editSpecies = rootView.findViewById(R.id.editSpecies);
        editGender = rootView.findViewById(R.id.editGender);
        editBirth = rootView.findViewById(R.id.editBirth);
        editWeight = rootView.findViewById(R.id.editWeight);
        editHabitat = rootView.findViewById(R.id.editHabitat);
        editSterilize = rootView.findViewById(R.id.editSterilize);
        btnEditProfile = rootView.findViewById(R.id.btnEditProfile);
        btnDeleteProfile = rootView.findViewById(R.id.btnDeleteProfile);
        btnLogOut = rootView.findViewById(R.id.btnLogOut);

        // Initialize FirebaseAuth
        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            String uid = currentUser.getUid();

            // Initialize Firebase reference
            profileRef = FirebaseDatabase.getInstance().getReference("Pets").child(uid);

            // Load profile data
            loadProfileData();

            // Set Edit button functionality
            btnEditProfile.setOnClickListener(v -> toggleEditMode());

            // Set Delete button functionality
            btnDeleteProfile.setOnClickListener(v -> deleteProfile());

            // Log Out button functionality
            btnLogOut.setOnClickListener(v -> {
                mAuth.signOut();
                Toast.makeText(getActivity(), "Logged out successfully", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().finish();
                }
            });

            // Add DatePicker to Birth field
            editBirth.setOnClickListener(v -> showDatePickerDialog());

            // Add dropdown menus to Gender, Habitat, and Sterilize fields
            setupDropdown(editGender, R.menu.gender_menu);
            setupDropdown(editHabitat, R.menu.habitat_menu);
            setupDropdown(editSterilize, R.menu.sterilize_menu);
        } else {
            Toast.makeText(getActivity(), "User not authenticated", Toast.LENGTH_SHORT).show();
            Log.e(TAG, "User not authenticated");
        }

        return rootView;
    }

    private void loadProfileData() {
        profileRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    PetProfile profile = snapshot.getValue(PetProfile.class);
                    if (profile != null) {
                        // Populate the fields with the retrieved data
                        editPetName.setText(profile.getPetName());
                        editSpecies.setText(profile.getSpecies());
                        editGender.setText(profile.getGender());
                        editBirth.setText(profile.getBirth());
                        editWeight.setText(profile.getWeight());
                        editHabitat.setText(profile.getHabitat());
                        editSterilize.setText(profile.getSterilize());

                        setEditMode(false);
                    }
                } else {
                    Toast.makeText(getActivity(), "Profile not found", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e(TAG, "Error loading data: " + error.getMessage());
            }
        });
    }

    private void setupDropdown(EditText editText, int menuResId) {
        editText.setOnClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(getActivity(), editText);
            popupMenu.getMenuInflater().inflate(menuResId, popupMenu.getMenu());
            popupMenu.setOnMenuItemClickListener(item -> {
                editText.setText(item.getTitle());
                return true;
            });
            popupMenu.show();
        });
    }

    private void toggleEditMode() {
        isEditing = !isEditing;

        if (isEditing) {
            btnEditProfile.setText("Save");
            setEditMode(true);
        } else {
            updateProfile();
        }
    }

    private void setEditMode(boolean enabled) {
        editPetName.setEnabled(enabled);
        editSpecies.setEnabled(enabled);
        editGender.setEnabled(enabled);
        editBirth.setEnabled(enabled);
        editWeight.setEnabled(enabled);
        editHabitat.setEnabled(enabled);
        editSterilize.setEnabled(enabled);
    }

    private void deleteProfile() {
        profileRef.removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Profile deleted successfully", Toast.LENGTH_SHORT).show();
                if (getActivity() != null) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }
            } else {
                Toast.makeText(getActivity(), "Failed to delete profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateProfile() {
        String petName = editPetName.getText().toString();
        String species = editSpecies.getText().toString();
        String gender = editGender.getText().toString();
        String birth = editBirth.getText().toString();
        String weight = editWeight.getText().toString();
        String habitat = editHabitat.getText().toString();
        String sterilize = editSterilize.getText().toString();

        PetProfile updatedProfile = new PetProfile(petName, species, gender, birth, weight, habitat, sterilize);

        profileRef.setValue(updatedProfile).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(getActivity(), "Profile updated successfully", Toast.LENGTH_SHORT).show();
                setEditMode(false);
                btnEditProfile.setText("Edit");
            } else {
                Toast.makeText(getActivity(), "Failed to update profile", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                getActivity(),
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
}
