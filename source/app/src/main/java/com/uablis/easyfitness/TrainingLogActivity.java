package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TrainingLogActivity extends AppCompatActivity {

    private LinearLayout routinesLayout;
    private TextView totalHoursTextView;
    private ImageView training_routines;
    private int totalSeconds = 0; // Total time in seconds

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_log);

        // Initialize your Views
        routinesLayout = findViewById(R.id.llRoutineList);
        totalHoursTextView = findViewById(R.id.tvTotalHours);
        training_routines = findViewById(R.id.training_routines);

        // Edge to edge UI handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout1), (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        training_routines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openMyRoutines();
            }
        });

        loadTrainingLogs();
    }

    private void openMyRoutines() {
        // Open the TrainingRoutinesActivity
        Intent intent = new Intent(TrainingLogActivity.this, TrainingRoutinesActivity.class);
        startActivity(intent);
    }

    private void loadTrainingLogs() {
        totalSeconds = 0; // Reset the total seconds
        routinesLayout.removeAllViews(); // Clear all routine views
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId()); // Get the current user ID
        String url = "http://172.17.176.1:8080/api/registros/user/" + userID; // Your API endpoint

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
                });

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
        int remainingSeconds = totalSeconds % 3600;
        int minutes = remainingSeconds / 60;
        remainingSeconds = remainingSeconds % 60; // Get the remaining seconds
        totalHoursTextView.setText(String.format("Total Hours: %02d:%02d:%02d", hours, minutes, remainingSeconds));
    }

    private void addRoutineView(String name, String time) {
        View routineEntryView = getLayoutInflater().inflate(R.layout.routine_entry, routinesLayout, false);
        TextView tvRoutineName = routineEntryView.findViewById(R.id.tvRoutineName);
        TextView tvTimeSpent = routineEntryView.findViewById(R.id.tvTimeSpent);

        tvRoutineName.setTextColor(getResources().getColor(R.color.white)); // Assuming you have a white color value defined
        tvTimeSpent.setTextColor(getResources().getColor(R.color.white));

        tvRoutineName.setText(name);
        tvTimeSpent.setText(time);

        routinesLayout.addView(routineEntryView);
    }
}