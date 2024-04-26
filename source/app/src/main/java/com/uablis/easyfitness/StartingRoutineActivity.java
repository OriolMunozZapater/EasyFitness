package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StartingRoutineActivity extends AppCompatActivity {
    private Chronometer trainingDuration;
    private Button endTraining;
    private LinearLayout routinesLayout;
    private long startTime;
    private int rutinaId;
    private String routineName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_routine);

        trainingDuration = findViewById(R.id.chronometer);

        endTraining = findViewById(R.id.btnEndTraining);
        routinesLayout = findViewById(R.id.exerciseTrainContainer);

        // rutinaId = 1;
        rutinaId = Integer.parseInt(getIntent().getStringExtra("ROUTINE_ID"));
        // String routineName = "bench press";
        obtenerRutinaName(rutinaId);

        updateUIWithRoutines(routineName);

        endTraining.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                endRoutine();
            }
        });

        trainingDuration.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
                int minutes = (int) (elapsedMillis / 60000);
                int seconds = (int) ((elapsedMillis % 60000) / 1000);
                chronometer.setText(String.format("%02dmin %02ds", minutes, seconds));
            }
        });

        // Iniciar el cronómetro
        trainingDuration.setBase(SystemClock.elapsedRealtime());
        trainingDuration.start();
    }


    private void updateUIWithRoutines(String routineName) {
        LinearLayout routinesLayout = findViewById(R.id.exerciseTrainContainer);

        View routineView = getLayoutInflater().inflate(R.layout.exercise_training, routinesLayout, false);
        TextView textView = routineView.findViewById(R.id.tvExerciseName);
        Button addSerie = routineView.findViewById(R.id.btnAddSerie);
        LinearLayout seriesContainer = routineView.findViewById(R.id.seriesContainer);

        textView.setText(routineName);

        addSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewSerie(seriesContainer);
            }
        });

        // Agregar un OnClickListener al ImageButton de la primera fila de serie por defecto
        ImageButton saveSerieFirstRow = routineView.findViewById(R.id.saveSerie);
        saveSerieFirstRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LinearLayout firstRow = (LinearLayout) seriesContainer.getChildAt(0);
                int newColor = ContextCompat.getColor(StartingRoutineActivity.this, R.color.green);
                firstRow.setBackgroundColor(newColor);
            }
        });

        // Agregar un OnClickListener al ImageButton para eliminar el ejercicio
        ImageButton eliminateExercise = routineView.findViewById(R.id.eliminate_cross);
        eliminateExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteExercise(routineView);
            }
        });

        routinesLayout.addView(routineView);
    }

    public void deleteExercise(View routineView) {
        // Mostrar un diálogo de confirmación antes de eliminar el ejercicio
        AlertDialog.Builder builder = new AlertDialog.Builder(StartingRoutineActivity.this);
        builder.setTitle("Eliminar ejercicio");
        builder.setMessage("¿Estás seguro de que quieres eliminar este ejercicio?");
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Obtener el padre del LinearLayout que contiene todo el diseño del ejercicio y eliminarlo
                ViewGroup parent = (ViewGroup) routineView.getParent();
                parent.removeView(routineView);
                dialog.dismiss(); // Cerrar el diálogo
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss(); // Cerrar el diálogo
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void addNewSerie(LinearLayout seriesContainer) {
        // Obtener el número de serie de la última fila agregada al contenedor
        int lastSerieNumber = seriesContainer.getChildCount();

        // Inflar la vista de fila de serie desde XML
        LinearLayout newRow = (LinearLayout) getLayoutInflater().inflate(R.layout.row_serie, null);

        // Alternar el color de fondo
        int bgColor = ContextCompat.getColor(this, lastSerieNumber % 2 == 0 ? R.color.layout_type1 : R.color.layout_type2);
        newRow.setBackgroundColor(bgColor);

        // Establecer ID único para la nueva fila
        newRow.setId(View.generateViewId());

        // Asignar un margen superior a la nueva fila
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 10, 0, 0);
        newRow.setLayoutParams(layoutParams);

        // Obtener el EditText para el número de serie y asignarle el valor del contador de serie
        EditText etNumSerie = newRow.findViewById(R.id.etNumSerie);
        etNumSerie.setText(String.valueOf(lastSerieNumber + 1)); // Aumentar el número de serie en uno

        ImageButton saveSerie = newRow.findViewById(R.id.saveSerie);
        saveSerie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Cambiar el color de fondo del LinearLayout newRow
                int newColor = ContextCompat.getColor(StartingRoutineActivity.this, R.color.green);
                newRow.setBackgroundColor(newColor);
            }
        });

        // Agregar la nueva fila al contenedor
        seriesContainer.addView(newRow);
    }


    public void endRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to end this training session?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //guardar les vaines a la bd
                long elapsedMillis = SystemClock.elapsedRealtime() - trainingDuration.getBase();
                String tiempoTardado = formatElapsedTime(elapsedMillis);
                insertRegistro(routineName, tiempoTardado);
                trainingDuration.stop();
                finish();
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, simplemente cerrar el diálogo
                dialog.dismiss();
            }
        });
        builder.show();
    }

    private String formatElapsedTime(long elapsedMillis) {
        int hours = (int) (elapsedMillis / 3600000);
        int minutes = (int) ((elapsedMillis % 3600000) / 60000);
        int seconds = (int) ((elapsedMillis % 60000) / 1000);
        return String.format("%02d:%02d:%02d", hours, minutes, seconds);
    }



    private void insertRegistro(final String nombreRutina, final String tiempoTardado) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.97:8080/api/registros";
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());

        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // La inserción fue exitosa
                        Toast.makeText(StartingRoutineActivity.this, "Rutina registrada exitosamente.", Toast.LENGTH_SHORT).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Solo notifica al usuario que hubo un error
                        Toast.makeText(StartingRoutineActivity.this, "Error al registrar la rutina.", Toast.LENGTH_SHORT).show();
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
                String reqBody = "{\"nombreRutina\":\"" + nombreRutina + "\", \"tiempoTardado\":\"" + tiempoTardado + "\", \"userID\":" + userID + "}";
                return reqBody.getBytes(StandardCharsets.UTF_8);
            }

        };
        queue.add(postRequest);
    }

    private void obtenerRutinaName(int rutinaId) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.97:8080/api/rutinas/" + rutinaId;

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    try {
                        routineName = response.getString("nombre");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            },
            new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // Solo notifica al usuario que hubo un error
                    Toast.makeText(StartingRoutineActivity.this, "Error al obtener el nombre de la rutina.", Toast.LENGTH_SHORT).show();
                }
            });
        queue.add(jsonObjectRequest);
    }
}