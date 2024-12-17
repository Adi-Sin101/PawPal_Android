package com.example.pawpal_18nov;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Sign_Up extends AppCompatActivity {

    EditText email, password;
    TextView redirecttxt;
    Button btn;
    ProgressBar signUpProgressBar;
    FirebaseAuth mAuth;

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            Intent intent = new Intent(getApplicationContext(), Logout.class);
            startActivity(intent);
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpProgressBar = findViewById(R.id.sprogressBar);
        mAuth = FirebaseAuth.getInstance();
        email = findViewById(R.id.semailEditText);
        password = findViewById(R.id.spasswordEditText);
        btn = findViewById(R.id.signUpButton);
        redirecttxt = findViewById(R.id.signInRedirectTextView);

        redirecttxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Sign_Up.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn.setOnClickListener(view -> {
            String e_mail = email.getText().toString();
            String pass_word = password.getText().toString();

            if (TextUtils.isEmpty(e_mail)) {
                Toast.makeText(Sign_Up.this, "Enter Email", Toast.LENGTH_SHORT).show();
                return;
            }
            if (TextUtils.isEmpty(pass_word)) {
                Toast.makeText(Sign_Up.this, "Enter Password", Toast.LENGTH_SHORT).show();
                return;
            }

            signUpProgressBar.setVisibility(View.VISIBLE);
            mAuth.createUserWithEmailAndPassword(e_mail, pass_word)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            signUpProgressBar.setVisibility(View.GONE);

                            if (task.isSuccessful()) {
                                FirebaseUser user = mAuth.getCurrentUser();
                                Log.d("SignUp", "User signed in: " + user.getEmail());
                                Toast.makeText(Sign_Up.this, "Account created successfully!", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(Sign_Up.this, Make_Profile.class); // Go to Make_Profile activity
                                startActivity(intent);
                                finish();
                            } else {
                                Toast.makeText(Sign_Up.this, "Sign-Up failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
        });
    }
}
