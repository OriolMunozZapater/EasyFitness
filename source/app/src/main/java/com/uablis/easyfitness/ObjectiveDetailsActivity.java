package com.uablis.easyfitness;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
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

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class ObjectiveDetailsActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private EditText tvTargetWeight, tvCurrentWeight, tvDescription;
    private TextView tvProgressLabel;
    private ProgressBar pbWeightProgress;
    private Button btnSaveChanges;
    private ImageView backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_objective_details);

        initializeViews();
        retrieveDataFromIntent();
        setupListeners();
        scheduleWeeklyNotification();
    }

    private void scheduleWeeklyNotification() {
        Intent intent = new Intent(this, NotificationReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);

        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.add(Calendar.WEEK_OF_YEAR, 1);  // Configura la alarma para dentro de una semana

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(),
                AlarmManager.INTERVAL_DAY * 7, pendingIntent);
    }

    private void initializeViews() {
        tvTargetWeight = findViewById(R.id.tvTargetWeight);
        tvCurrentWeight = findViewById(R.id.tvCurrentWeight);
        tvDescription = findViewById(R.id.tvDescription);
        tvProgressLabel = findViewById(R.id.tvProgressLabel);
        pbWeightProgress = findViewById(R.id.pbWeightProgress);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);
        backButton = findViewById(R.id.backButton);
    }

    private void retrieveDataFromIntent() {
        Intent intent = getIntent();
        if (intent != null) {
            String currentWeight = intent.getStringExtra("CURRENT_WEIGHT");
            String targetWeight = intent.getStringExtra("TARGET_WEIGHT");
            String description = intent.getStringExtra("DESCRIPTION");

            if(currentWeight != null) {
                tvCurrentWeight.setText(currentWeight);
            }
            if(targetWeight != null) {
                tvTargetWeight.setText(targetWeight);
            }
            if(description != null) {
                tvDescription.setText(description);
            }

            updateProgressBarBasedOnWeights(currentWeight, targetWeight);
        }
    }

    private void updateProgressBarBasedOnWeights(String currentWeightStr, String targetWeightStr) {
        double currentWeight = Double.parseDouble(currentWeightStr.split(" ")[0].replace(",", "."));
        double targetWeight = Double.parseDouble(targetWeightStr.split(" ")[0].replace(",", "."));

        double progressPercentage;
        if (currentWeight >= targetWeight) {
            progressPercentage = 100;
        } else {
            progressPercentage = (currentWeight / targetWeight) * 100; // Porcentaje de progreso hacia el objetivo
        }
        pbWeightProgress.setProgress((int) progressPercentage); // Establece el progreso actual
        tvProgressLabel.setText(String.format(Locale.getDefault(), "Progress: %.2f%% of goal achieved", progressPercentage));
    }
    private void setupListeners() {

        btnSaveChanges.setOnClickListener(v -> saveObjectiveDetails());
        backButton.setOnClickListener(v -> startActivity(new Intent(ObjectiveDetailsActivity.this, ProfileActivity.class)));
    }

    private void saveObjectiveDetails() {
        RequestQueue queue = Volley.newRequestQueue(this);
        Integer userId = UsuarioActual.getInstance().getUserIdAsInteger();
        String path = "usuarios/" + userId + "/updateWithObjective";
        String url = urlBase.buildUrl(path);

        String currentWeightClean = tvCurrentWeight.getText().toString().split(" ")[0].replace(",", ".");
        String targetWeightClean = tvTargetWeight.getText().toString().split(" ")[0].replace(",", ".");

        JSONObject params = new JSONObject();
        try {
            params.put("peso_objetivo", Double.parseDouble(targetWeightClean));
            params.put("peso_actual", Double.parseDouble(currentWeightClean));
            params.put("descripcion", tvDescription.getText().toString());
        } catch (JSONException e) {
            Toast.makeText(this, "Error creating JSON params", Toast.LENGTH_SHORT).show();
            return;
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PUT, url, params,
                response -> {
                    Toast.makeText(ObjectiveDetailsActivity.this, "Update successful", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ObjectiveDetailsActivity.this, ProfileActivity.class);
                    startActivity(intent);
                },
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