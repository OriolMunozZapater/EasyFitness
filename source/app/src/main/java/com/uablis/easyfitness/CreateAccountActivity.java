package com.uablis.easyfitness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import java.io.UnsupportedEncodingException;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class CreateAccountActivity extends AppCompatActivity {

    private EditText etNewEmail, etNewPassword, etConfirmPassword;
    private Button btnSubmitCreateAccount;
    private Toolbar toolbar;
    private ImageView backArrow;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);

        etNewEmail = findViewById(R.id.etNewEmail);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSubmitCreateAccount = findViewById(R.id.btnSubmitCreateAccount);

        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.back_arrow);

        mAuth = FirebaseAuth.getInstance();

        backArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backPressed();
            }
        });

        btnSubmitCreateAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = etNewEmail.getText().toString().trim();
                String password = etNewPassword.getText().toString().trim();
                String confirmPassword = etConfirmPassword.getText().toString().trim();
                createAccount(email, password, confirmPassword);
            }
        });
    }

    private void backPressed() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        startActivity(intent);
    }

    private void createAccount(final String email, String password, String confirmPassword) {
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showAlert("Error", "All fields are required.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showAlert("Error", "Passwords do not match.");
            return;
        }

        if (password.length() < 6) {
            showAlert("Error", "Password must be at least 6 characters long.");
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            insertUserIntoApi(email, password);
                        } else {
                            String errorMessage = task.getException() != null ? task.getException().getMessage() : "An error occurred";
                            showAlert("Registration Failed", errorMessage);
                        }
                    }
                });
    }

    private void insertUserIntoApi(final String email, final String password) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.97:8080/api/usuarios";

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // La inserción fue exitosa
                        showAlert("Success", "Account created successfully and data saved to API.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Imprime detalles del error
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String body;
                            //Obtiene el cuerpo de la respuesta
                            final String statusCode = String.valueOf(error.networkResponse.statusCode);
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                showAlert("API Error", "Failed to save user data to API. Status Code: " + statusCode + " Body: " + body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showAlert("API Error", "Failed to save user data to API. Error: " + error.toString());
                        }
                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }

            @Override
            public byte[] getBody() throws AuthFailureError {
                String reqBody = "{\"correo\":\"" + email + "\", \"password\":\"" + password + "\"}";
                return reqBody.getBytes(StandardCharsets.UTF_8);
            }
        };
        queue.add(postRequest);
    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(CreateAccountActivity.this, R.style.AlertDialogTheme);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // Redirige al usuario a la pantalla de inicio de sesión
                        navigateToLogin();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void navigateToLogin() {
        Intent intent = new Intent(CreateAccountActivity.this, LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK); // Limpia el back stack
        startActivity(intent);
    }

}
