package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ObjectiveDetailsActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private EditText tvTargetWeight, tvCurrentWeight, tvDescription;
    private TextView tvProgressLabel;
    private ProgressBar pbWeightProgress;
    private Button btnSaveChanges;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objective_details);

        initializeViews();
        retrieveDataFromIntent();
        setupListeners();
    }

    private void initializeViews() {
        tvTargetWeight = findViewById(R.id.tvTargetWeight);
        tvCurrentWeight = findViewById(R.id.tvCurrentWeight);
        tvDescription = findViewById(R.id.tvDescription);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        pbWeightProgress = findViewById(R.id.pbWeightProgress);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
    }

    private void retrieveDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String currentWeight = intent.getStringExtra("CURRENT_WEIGHT");
            String targetWeight = intent.getStringExtra("TARGET_WEIGHT");
            String description = intent.getStringExtra("DESCRIPTION");

            if (currentWeight != null && targetWeight != null) {
                tvCurrentWeight.setText(currentWeight);
                tvTargetWeight.setText(targetWeight);
                updateProgressBarBasedOnWeights(currentWeight, targetWeight);
            } else {
                Toast.makeText(this, "Error retrieving weight data.", Toast.LENGTH_SHORT).show();
            }
            if (description != null && !description.isEmpty()) {
                tvDescription.setText(description);
            } else {
                tvDescription.setText("No description provided.");
            }
        }
    }

    private void updateProgressBarBasedOnWeights(String currentWeightStr, String targetWeightStr) {
        double currentWeight = Double.parseDouble(currentWeightStr.split(" ")[0].replace(",", "."));
        double targetWeight = Double.parseDouble(targetWeightStr.split(" ")[0].replace(",", "."));

        double progress = ((currentWeight - targetWeight) / targetWeight) * 100;
        pbWeightProgress.setProgress((int) Math.abs(progress));
        tvProgressLabel.setText(String.format(Locale.getDefault(), "Progress: %d%% left to goal", (int) (100 - Math.abs(progress))));
    }

    private void setupListeners() {
        btnSaveChanges.setOnClickListener(v -> saveObjectiveDetails());
    }

    private void saveObjectiveDetails() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String path = "usuarios/" + userId + "/updateWithObjective";
        String url = urlBase.buildUrl(path);

        // Extraer solo la parte numérica de los pesos antes de enviar
        String currentWeightClean = tvCurrentWeight.getText().toString().split(" ")[0].replace(",", ".");
        String targetWeightClean = tvTargetWeight.getText().toString().split(" ")[0].replace(",", ".");

        JSONObject params = new JSONObject();
        try {
            params.put("peso_objetivo", Double.parseDouble(targetWeightClean));
            params.put("peso_actual", Double.parseDouble(currentWeightClean));
            params.put("descripcion_objetivo", tvDescription.getText().toString());
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating JSON params", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, params,
                response -> Toast.makeText(ObjectiveDetailsActivity.this, "Update successful", Toast.LENGTH_SHORT).show(),
                error -> Toast.makeText(ObjectiveDetailsActivity.this, "Error in update: " + error.toString(), Toast.LENGTH_SHORT).show()
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

}