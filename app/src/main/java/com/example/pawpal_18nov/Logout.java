package com.example.pawpal_18nov;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Logout extends AppCompatActivity {

    //private static final android.R.attr R = ;
    ProgressBar LogoutProgressBar;
    FirebaseAuth mAuth ;
    Button btn_log;
    TextView showEmail;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        this.setTitle("Insert Data"); //inside main activity class
        this.setTitle("Logout"); //inside main activity class
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_logout);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        //My code
        mAuth=FirebaseAuth.getInstance();
        FirebaseUser user = mAuth.getCurrentUser();
        btn_log=findViewById(R.id.logout);
        showEmail=findViewById(R.id.details);
        //User stores info associated with the user currently loged in
        if(user == null)
        {
            Intent intent=new Intent(Logout.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
        else{
            showEmail.setText(user.getEmail());
        }

        btn_log.setOnClickListener(view -> {
            mAuth.signOut();
            Intent intent=new Intent(Logout.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

    }
}