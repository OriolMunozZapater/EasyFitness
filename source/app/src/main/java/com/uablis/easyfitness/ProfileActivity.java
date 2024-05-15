package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Locale;

public class ProfileActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private TextView pesObjectiu, pesPropi, nomUser, sexe, altura;
    private ImageButton edit_profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        getUserData();
        getObjectiveData();
        setupListeners();
    }

    private void initializeViews() {
        pesObjectiu = findViewById(R.id.targetWeight);
        pesPropi = findViewById(R.id.currentWeight);
        nomUser = findViewById(R.id.nomUser);
        sexe = findViewById(R.id.sexe);
        altura = findViewById(R.id.altura);
        edit_profile = findViewById(R.id.btnEditPersonalData);
    }

    private void setupListeners() {
        edit_profile.setOnClickListener(v -> goToEditData());
        findViewById(R.id.boxint1).setOnClickListener(v -> goToObjectiveDetails());
        findViewById(R.id.training_routines).setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, TrainingRoutinesActivity.class)));
    }

    private void goToEditData() {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }


    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            nomUser.setText(jsonObject.getString("nombre"));
            sexe.setText(jsonObject.getString("sexo"));
            pesPropi.setText(String.format(Locale.getDefault(), "%.2f Kg", jsonObject.getDouble("peso_actual")));
            altura.setText(String.format(Locale.getDefault(), "%d cm", jsonObject.getInt("altura")));
        } catch (JSONException e) {
            Toast.makeText(ProfileActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleObjectiveResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            if (jsonObject.has("pesoObjetivo")) {
                double targetWeight = jsonObject.getDouble("pesoObjetivo");
                pesObjectiu.setText(String.format(Locale.getDefault(), "%.2f Kg", targetWeight));
            } else {
                Toast.makeText(ProfileActivity.this, "Peso objetivo not available", Toast.LENGTH_SHORT).show();
            }
        } catch (JSONException e) {
            Toast.makeText(ProfileActivity.this, "Error parsing objective data", Toast.LENGTH_SHORT).show();
        }
    }

    private void getUserData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String path = "usuarios/" + userId;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        handleResponse(String.valueOf(new JSONObject(response)));
                    } catch (JSONException e) {
                        Toast.makeText(ProfileActivity.this, "Error making API call: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ProfileActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private void getObjectiveData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String path = "usuarios/" + userId + "/objetivo";
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        handleObjectiveResponse(String.valueOf(new JSONObject(response)));
                    } catch (JSONException e) {
                        Toast.makeText(ProfileActivity.this, "Error making API call: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(ProfileActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private void goToObjectiveDetails() {
        Intent intent = new Intent(this, ObjectiveDetailsActivity.class);
        // Agregar datos extras al Intent
        String currentWeight = pesPropi.getText().toString();
        String targetWeight = pesObjectiu.getText().toString();
        intent.putExtra("CURRENT_WEIGHT", currentWeight);
        intent.putExtra("TARGET_WEIGHT", targetWeight);

        startActivity(intent);
    }

}
