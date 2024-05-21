package com.uablis.easyfitness;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class FirstLoginActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private EditText etCurrentWeight, etTargetWeight, etName, etSecondName, etHeight;
    private TextView etDateOfBirth;
    private Button btnSexSelect;
    private ImageButton forwardArrow;
    private CharSequence[] genderOptions = {"Masculino", "Femenino", "Otros", "Prefiero no decirlo"};
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_login);

        bindViews();
        setupListeners();
    }

    private void bindViews() {
        etCurrentWeight = findViewById(R.id.etCurrentWeight);
        etTargetWeight = findViewById(R.id.etTargetWeight);
        etName = findViewById(R.id.etName);
        etSecondName = findViewById(R.id.etSecondName);
        etHeight = findViewById(R.id.etHeight);
        etDateOfBirth = findViewById(R.id.etDateOfBirth);
        btnSexSelect = findViewById(R.id.btnSexSelect);
        forwardArrow = findViewById(R.id.forwardArrow);
        toolbar = findViewById(R.id.toolbar);
    }

    private void setupListeners() {
        forwardArrow.setOnClickListener(v -> nextScreen());
        etDateOfBirth.setOnClickListener(v -> showDatePickerDialog());
        btnSexSelect.setOnClickListener(this::showSexoOptions);
    }

    private void showDatePickerDialog() {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                FirstLoginActivity.this,
                (view, year, month, dayOfMonth) -> etDateOfBirth.setText(String.format("%d-%02d-%02d", year, month + 1, dayOfMonth)),
                calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    public void showSexoOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona tu sexo");
        builder.setItems(genderOptions, (dialog, which) -> btnSexSelect.setText(genderOptions[which]));
        builder.show();
    }

    public void nextScreen() {
        if (validateFields()) {
            updateUserInfo();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Error");
            builder.setMessage("Por favor, rellena todos los campos antes de continuar.");
            builder.setPositiveButton("OK", null);
            builder.show();
        }
    }

    private boolean validateFields() {
        return !etName.getText().toString().trim().isEmpty() &&
                !etSecondName.getText().toString().trim().isEmpty() &&
                !etCurrentWeight.getText().toString().trim().isEmpty() &&
                !etTargetWeight.getText().toString().trim().isEmpty() &&
                !etHeight.getText().toString().trim().isEmpty() &&
                !etDateOfBirth.getText().toString().isEmpty() &&
                !btnSexSelect.getText().toString().equals("Select");
    }

    private void updateUserInfo() {
        String userId = UsuarioActual.getInstance().getUserId();
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nombre", etName.getText().toString().trim());
            jsonBody.put("apellido", etSecondName.getText().toString().trim());
            jsonBody.put("peso_actual", etCurrentWeight.getText().toString().trim());
            jsonBody.put("peso_objetivo", etTargetWeight.getText().toString().trim());
            jsonBody.put("altura", etHeight.getText().toString().trim());
            jsonBody.put("fecha_nacimiento", etDateOfBirth.getText().toString());
            jsonBody.put("sexo", btnSexSelect.getText().toString());
            jsonBody.put("firstLogin", false);

            sendUpdateRequest(jsonBody);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void sendUpdateRequest(JSONObject jsonBody) {
        String userId = UsuarioActual.getInstance().getUserId();
        String url = urlBase.buildUrl("usuarios/" + userId + "/updateWithObjective");

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.PUT, url, jsonBody,
                response -> {
                    Toast.makeText(FirstLoginActivity.this, "User and Objective updated successfully!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(FirstLoginActivity.this, TrainingRoutinesActivity.class);
                    startActivity(intent);
                    finish();
                },
                error -> {
                    Log.e("API Error", "Error: " + error.toString());
                    if (error.networkResponse != null) {
                        Log.e("API Error Status Code", String.valueOf(error.networkResponse.statusCode));
                    }
                    Toast.makeText(FirstLoginActivity.this, "Failed to update user and create objective: " + error.getMessage(), Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

}
