package com.uablis.easyfitness;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AlertDialog;
import com.google.firebase.auth.FirebaseAuth;

public class PasswordRecoveryActivity extends AppCompatActivity {

    private EditText etRecoveryEmail;
    private Button btnSendRecoveryEmail;
    private Toolbar toolbar;
    private ImageView backArrow;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_recovery);

        etRecoveryEmail = findViewById(R.id.etRecoveryEmail);
        btnSendRecoveryEmail = findViewById(R.id.btnSendRecoveryEmail);

        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.back_arrow);

        mAuth = FirebaseAuth.getInstance();

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });

        btnSendRecoveryEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = etRecoveryEmail.getText().toString().trim();
                if (!email.isEmpty()) {
                    sendRecoveryEmail(email);
                } else {
                    Toast.makeText(PasswordRecoveryActivity.this, "Please enter your email", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void sendRecoveryEmail(String email) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(task -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(PasswordRecoveryActivity.this);
            builder.setPositiveButton(android.R.string.ok, (dialog, which) -> {
                if (task.isSuccessful()) {
                    finish();
                }
            });

            if (task.isSuccessful()) {
                builder.setMessage("Recovery email sent. Please check your email.");
                builder.setTitle("Email Sent");
                backToLogin();
            } else {
                String errorMessage = task.getException() != null ? task.getException().getMessage() : "An unknown error occurred";
                builder.setMessage("Failed to send recovery email: " + errorMessage);
                builder.setTitle("Error");
            }

            builder.show();
        });
    }
    private void backToLogin() {
        Intent intent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void backPressed() {
        Intent intent = new Intent(PasswordRecoveryActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
