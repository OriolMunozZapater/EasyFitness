package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

public class TrainingRecordsActivity extends AppCompatActivity {
    private LinearLayout llRoutineList;
    private TextView tvTotalHours;
    private int totalSeconds = 0; // Total time in seconds
    private int userID;
    private ImageView home, trainingRoutinesButton, profile, training_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_log2);

        llRoutineList = findViewById(R.id.llRoutineList);
        tvTotalHours = findViewById(R.id.tvTotalHours);

        // Retrieve the user ID from Intent
        userID = getIntent().getIntExtra("userID", -1);  // Recibir como entero

        if (userID == -1) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
        }

        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingRecordsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingRecordsActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(TrainingRecordsActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(TrainingRecordsActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        loadTrainingLogs();
    }


    private void loadTrainingLogs() {
        totalSeconds = 0; // Reset the total seconds
        llRoutineList.removeAllViews(); // Clear all routine views

        String path = "registros/user/" + userID;
        String url = new ApiUrlBuilder().buildUrl(path);

        // Instantiate the RequestQueue
        RequestQueue queue = Volley.newRequestQueue(this);

        // Request a jsonArray response from the provided URL
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject registro = response.getJSONObject(i);
                                String routineName = registro.getString("nombreRutina");
                                String timeSpent = registro.getString("tiempoTardado");

                                // Parse time and accumulate total seconds
                                totalSeconds += parseTime(timeSpent);

                                addRoutineView(routineName, timeSpent);
                            }
                            // Update total hours after processing all routines
                            updateTotalHoursView();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        // Add the request to the RequestQueue
        queue.add(jsonArrayRequest);
    }

    private int parseTime(String time) {
        String[] units = time.split(":"); // HH:MM:SS
        int hours = Integer.parseInt(units[0]);
        int minutes = Integer.parseInt(units[1]);
        int seconds = Integer.parseInt(units[2]);
        return 3600 * hours + 60 * minutes + seconds; // Convert to total seconds
    }

    private void updateTotalHoursView() {
        int hours = totalSeconds / 3600;
        int minutes = (totalSeconds % 3600) / 60;
        int seconds = totalSeconds % 60;
        tvTotalHours.setText(String.format("Total Hours: %02d:%02d:%02d", hours, minutes, seconds));
    }

    private void addRoutineView(String name, String time) {
        View routineEntryView = getLayoutInflater().inflate(R.layout.routine_entry, llRoutineList, false);
        TextView tvRoutineName = routineEntryView.findViewById(R.id.tvRoutineName);
        TextView tvTimeSpent = routineEntryView.findViewById(R.id.tvTimeSpent);

        tvRoutineName.setText(name);
        tvTimeSpent.setText(time);

        llRoutineList.addView(routineEntryView);
    }


}
