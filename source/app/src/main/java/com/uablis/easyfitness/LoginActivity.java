package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import org.json.JSONException;

public class LoginActivity extends AppCompatActivity {

    private EditText etEmail, etPassword;
    private Button btnLogin, btnCreateAccount, btnRecoverPassword;
    private Toolbar toolbar;
    private ImageView backArrow;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        etEmail = findViewById(R.id.etNewEmail);
        etPassword = findViewById(R.id.etNewPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnCreateAccount = findViewById(R.id.btnCreateAccount);
        btnRecoverPassword = findViewById(R.id.btnRecoverPassword);

        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.back_arrow);
        mAuth = FirebaseAuth.getInstance();

        //listener back arrow
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backPressed();
            }
        });
        // Listener para el botón de Login
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etEmail.getText().toString();
                String password = etPassword.getText().toString();
                login(email, password);
            }
        });

        // Listener para el botón de Crear Cuenta
        btnCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openCreateAccountScreen();
            }
        });

        btnRecoverPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { openPasswordRecoveryScreen(); }
        });
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("Login", "signInWithEmail:success");
                            checkFirstLogin(email);
                        } else {
                            Log.w("Login", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    private void checkFirstLogin(String email) {
        String url = "http://192.168.1.97:8080/api/usuarios/firstlogin?email=" + email;

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        boolean isFirstLogin = response.getBoolean("isFirstLogin");
                        if (isFirstLogin) {
                            openFirstLoginActivity();
                        } else {
                            openViewTrainingRoutineActivity();
                        }
                    } catch (JSONException e) {
                        Log.e("LoginActivity", "JSON parsing error: " + e.getMessage());
                    }
                },
                error -> {
                    Log.e("API Error", "Error checking first login: " + error.toString());
                });

        queue.add(jsonObjectRequest);
    }

    private void openFirstLoginActivity() {
        Intent intent = new Intent(LoginActivity.this, FirstLoginActivity.class);
        startActivity(intent);
        finish();
    }

    private void openViewTrainingRoutineActivity() {
        Intent intent = new Intent(LoginActivity.this, TrainingRoutinesActivity.class);
        startActivity(intent);
        finish();
    }

    private void openCreateAccountScreen() {
        Intent intent = new Intent(LoginActivity.this, CreateAccountActivity.class);
        startActivity(intent);
    }

    private void openPasswordRecoveryScreen() {
        Intent intent = new Intent(LoginActivity.this, PasswordRecoveryActivity.class);
        startActivity(intent);
    }
    private void backPressed() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
    }
}