package com.uablis.easyfitness;

import android.content.Intent;
import android.graphics.Color;
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
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class TrainingLogActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private LinearLayout routinesLayout;
    private TextView totalHoursTextView;
    private int totalSeconds = 0; // Total time in seconds
    private ImageView home, trainingRoutinesButton, profile, training_session;
    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_log);

        // Initialize your Views
        routinesLayout = findViewById(R.id.llRoutineList);
        totalHoursTextView = findViewById(R.id.tvTotalHours);
        barChart = findViewById(R.id.barChart);


        // Edge to edge UI handling
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.layout1), (v, insets) -> {
            Insets systemBarsInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBarsInsets.left, systemBarsInsets.top, systemBarsInsets.right, systemBarsInsets.bottom);
            return WindowInsetsCompat.CONSUMED;
        });

        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingLogActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TrainingLogActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(TrainingLogActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(TrainingLogActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        loadTrainingLogs();
    }

    private void loadTrainingLogs() {
        totalSeconds = 0; // Reset the total seconds
        routinesLayout.removeAllViews(); // Clear all routine views
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        String path = "registros/user/" + userID;
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<BarEntry> entries = new ArrayList<>();
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject registro = response.getJSONObject(i);
                                String routineName = registro.getString("nombreRutina");
                                String timeSpent = registro.getString("tiempoTardado");
                                int timeInSeconds = parseTime(timeSpent);
                                totalSeconds += timeInSeconds;

                                addRoutineView(routineName, timeSpent);
                                entries.add(new BarEntry(i, timeInSeconds));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        updateTotalHoursView();
                        loadBarChart(entries);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                });

        queue.add(jsonArrayRequest);
    }

    private void loadBarChart(List<BarEntry> entries) {
        BarDataSet dataSet = new BarDataSet(entries, "Tiempo de Entrenamiento");
        dataSet.setColor(Color.BLUE);

        BarData barData = new BarData(dataSet);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false);
        barChart.animateY(500);
        barChart.setBackgroundColor(Color.WHITE); // Establece el fondo del gráfico a blanco

        // Configuración de los ejes
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(entries.size());
        xAxis.setLabelRotationAngle(0); // Sin rotación para números

        YAxis leftAxis = barChart.getAxisLeft();
        YAxis rightAxis = barChart.getAxisRight();
        leftAxis.setDrawGridLines(true);
        rightAxis.setDrawGridLines(false);

        // Configuración de texto para los ejes
        leftAxis.setAxisMinimum(0f); // Empieza desde cero
        leftAxis.setDrawLabels(true); // Muestra las etiquetas
        xAxis.setDrawLabels(true); // Muestra las etiquetas en el eje X
        rightAxis.setDrawLabels(false); // No muestra etiquetas en el eje derecho

        // Formateador para el eje X para usar solo índices
        xAxis.setValueFormatter(new IndexAxisValueFormatter(getXAxisValues(entries.size())));

        barChart.invalidate(); // Refresh
    }

    private List<String> getXAxisValues(int count) {
        List<String> labels = new ArrayList<>();
        for (int i = 1; i <= count; i++) {
            labels.add("Registro " + i);
        }
        return labels;
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