package com.example.pawpal_18nov;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;
public class MainActivity extends AppCompatActivity {

    // UI elements
    private EditText email, password;

    // Firebase Auth instance
    private FirebaseAuth mAuth;

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is already signed in
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d("AuthCheck", "User already signed in. Redirecting to Home Page.");
            startActivity(new Intent(MainActivity.this, Home_Page.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("Insert Data"); //inside main activity class
        super.onCreate(savedInstanceState);
        this.setTitle("Sign In");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        // Adjust padding for system insets
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize FirebaseAuth instance
        mAuth = FirebaseAuth.getInstance();

        // Initialize UI components
        email = findViewById(R.id.emailEditText);
        password = findViewById(R.id.passwordEditText);
        Button btnSignIn = findViewById(R.id.signInButton);
        TextView redirectTxt = findViewById(R.id.signUpRedirectTextView);
        TextView forgotPasswordTxt = findViewById(R.id.forgotPasswordTextView);  // Forgot Password TextView

        // Redirect to Sign-Up Activity
        redirectTxt.setOnClickListener(view -> {
            Log.d("Redirect", "Redirecting to Sign-Up activity.");
            startActivity(new Intent(MainActivity.this, Sign_Up.class));
            finish();
        });

        // Handle Forgot Password functionality
        forgotPasswordTxt.setOnClickListener(view -> {
            String emailInput = email.getText().toString().trim();
            if (TextUtils.isEmpty(emailInput)) {
                Toast.makeText(MainActivity.this, "Please enter your email address.", Toast.LENGTH_SHORT).show();
                return;
            }
            sendPasswordResetEmail(emailInput);
        });

        // Set onClickListener for the Sign-In button
        btnSignIn.setOnClickListener(v -> signInUser());
    }

    private void signInUser() {
        // Retrieve user input
        String emailInput = email.getText().toString().trim();
        String passwordInput = password.getText().toString().trim();

        // Validate user input
        if (TextUtils.isEmpty(emailInput)) {
            Toast.makeText(MainActivity.this, "Enter Email", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(passwordInput)) {
            Toast.makeText(MainActivity.this, "Enter Password", Toast.LENGTH_SHORT).show();
            return;
        }

        // Sign in the user with Firebase Authentication
        Log.d("SignIn", "Attempting sign-in for email: " + emailInput);
        mAuth.signInWithEmailAndPassword(emailInput, passwordInput)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign-in successful, redirect to HomeActivity
                        Log.d("SignIn", "Sign-in successful. Redirecting to HomeActivity.");
                        Toast.makeText(MainActivity.this, "Sign-in successful!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(MainActivity.this, Home_Page.class));
                        finish();
                    } else {
                        // Sign-in failed, show error message
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Log.e("SignInError", "Authentication failed: " + errorMessage);
                        Toast.makeText(MainActivity.this, "Authentication Failed: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void sendPasswordResetEmail(String emailAddress) {
        mAuth.sendPasswordResetEmail(emailAddress)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        Toast.makeText(MainActivity.this, "Password reset email sent!", Toast.LENGTH_SHORT).show();
                    } else {
                        String errorMessage = Objects.requireNonNull(task.getException()).getMessage();
                        Log.e("PasswordResetError", "Failed to send password reset email: " + errorMessage);
                        Toast.makeText(MainActivity.this, "Failed to send reset email: " + errorMessage, Toast.LENGTH_SHORT).show();
                    }
                });
    }
}

