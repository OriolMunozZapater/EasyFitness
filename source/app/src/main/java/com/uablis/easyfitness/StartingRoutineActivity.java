package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.LayoutInflater;
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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class StartingRoutineActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private Chronometer trainingDuration;
    private Button endTraining;
    private LinearLayout routinesLayout;
    private ImageButton stopTime;
    private ImageButton goTime;
    private long startTime;
    private int rutinaId;
    private String rutinaID;
    private boolean isChronometerRunning;
    private String routineName;
    private long pauseOffset;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_routine);

        trainingDuration = findViewById(R.id.chronometer);
        endTraining = findViewById(R.id.btnEndTraining);
        routinesLayout = findViewById(R.id.exerciseTrainContainer);
        rutinaID = getIntent().getStringExtra("ROUTINE_ID");
        rutinaId = Integer.parseInt(getIntent().getStringExtra("ROUTINE_ID"));
        stopTime = findViewById(R.id.stopTime);
        goTime = findViewById(R.id.goTime);
        obtenerEjerciciosPorRutinaId(rutinaId);
        isChronometerRunning = true;
        pauseOffset = 0;

        endTraining.setOnClickListener(v -> endRoutine());

        trainingDuration.setOnChronometerTickListener(chronometer -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            int minutes = (int) (elapsedMillis / 60000);
            int seconds = (int) ((elapsedMillis % 60000) / 1000);
            chronometer.setText(String.format("%02dmin %02ds", minutes, seconds));
        });

        trainingDuration.setBase(SystemClock.elapsedRealtime());
        trainingDuration.start();

        stopTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseChronometer();
            }
        });

        goTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resumeChronometer();
            }
        });

    }

    private void pauseChronometer() {
        if (isChronometerRunning) {
            trainingDuration.stop();
            pauseOffset = SystemClock.elapsedRealtime() - trainingDuration.getBase();
            isChronometerRunning = false;
        }
    }

    private void resumeChronometer() {
        trainingDuration.setBase(SystemClock.elapsedRealtime() - pauseOffset);
        trainingDuration.start();
        isChronometerRunning = true;
    }

    private void updateUIWithExercises(JSONArray ejercicios) {
        try {
            for (int i = 0; i < ejercicios.length(); i++) {
                JSONObject ejercicio = ejercicios.getJSONObject(i);
                String ejercicioNombre = ejercicio.optString("nombre", "Nombre desconocido");
                String descripcion = ejercicio.optString("descripcion", "Descripción no disponible");
                String rutinaID = ejercicio.optString("rutinaID", "0"); // Valor predeterminado "0"

                // Inflar la vista del ejercicio y configurar sus elementos
                View exerciseView = getLayoutInflater().inflate(R.layout.exercise_training, routinesLayout, false);

                // Establecer el nombre del ejercicio
                TextView textView = exerciseView.findViewById(R.id.tvExerciseName);
                textView.setText(ejercicioNombre);

                // Establecer la descripción del ejercicio
                TextView descriptionView = exerciseView.findViewById(R.id.tvExerciseDescription);
                descriptionView.setText(descripcion);

                // Configurar el contenedor para series
                LinearLayout seriesContainer = exerciseView.findViewById(R.id.seriesContainer);

                // Configurar botón para añadir series
                Button addSerie = exerciseView.findViewById(R.id.btnAddSerie);
                addSerie.setOnClickListener(v -> addNewSerie(seriesContainer));

                // Configurar el botón para guardar series
                ImageButton saveSerie = exerciseView.findViewById(R.id.saveSerie);
                saveSerie.setOnClickListener(v -> {
                    EditText etPesSerie = exerciseView.findViewById(R.id.etPesSerie);
                    EditText etRepsSerie = exerciseView.findViewById(R.id.etRepsSerie);
                    String peso = etPesSerie.getText().toString();
                    String reps = etRepsSerie.getText().toString();

                    if (peso.isEmpty() || reps.isEmpty()) {
                        Toast.makeText(StartingRoutineActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    obtenerEjercicioId(ejercicioNombre, peso, reps, rutinaID);

                    // Visual feedback for first series row
                    LinearLayout firstRow = (LinearLayout) seriesContainer.getChildAt(0);
                    int newColor = ContextCompat.getColor(StartingRoutineActivity.this, R.color.green);
                    firstRow.setBackgroundColor(newColor);
                });

                // Configurar botón para eliminar ejercicio
                ImageButton eliminateExercise = exerciseView.findViewById(R.id.eliminate_cross);
                eliminateExercise.setOnClickListener(v -> deleteExercise(exerciseView));

                ImageButton typeSerie = exerciseView.findViewById(R.id.typeSerie);
                typeSerie.setOnClickListener(v -> showMessage(typeSerie));

                // Añadir la vista del ejercicio al layout principal
                routinesLayout.addView(exerciseView);
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error al procesar datos de ejercicios", Toast.LENGTH_SHORT).show();
        }
    }

    private void obtenerEjerciciosPorRutinaId(int rutinaID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "ejercicios/rutina/" + rutinaID;
        String url = urlBase.buildUrl(path);

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> updateUIWithExercises(response),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error al obtener ejercicios", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(getRequest);
    }

    private void obtenerEjercicioId(String ejercicioNombre, String peso, String reps, String rutinaID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "ejercicios/getEjercicioID?nom=" + ejercicioNombre + "&rutinaID=" + rutinaID;
        String url = urlBase.buildUrl(path);

        // Realiza la solicitud GET para obtener el ejercicioid
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener <JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el ejercicioid del objeto JSON de respuesta
                            String ejercicioid = response.getString("ejercicioID");
                            // Llamar a la función para guardar la serie con el ejercicioid obtenido
                            guardarSerieConEjercicioId(ejercicioid, peso, reps);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            // Manejo de errores en caso de problemas al analizar la respuesta JSON
                            showAlert("Error", "Error al analizar la respuesta JSON.");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores en caso de fallo en la solicitud GET
                        error.printStackTrace();
                        showAlert("Error", "Error en la solicitud GET para obtener el ejercicioid.");
                    }
                }
        );

        // Añadir la solicitud GET a la cola
        queue.add(getRequest);
    }

    private void guardarSerieConEjercicioId(String ejercicioid, String peso, String reps) {
        // Construye el cuerpo de la solicitud en formato JSON con el ejercicioid
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("peso", peso);
            jsonBody.put("n_repeticiones", reps);
            jsonBody.put("ejercicioID", ejercicioid);
            // Agrega otros campos necesarios como nombre de la rutina, descripción, etc.
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL para la solicitud POST
        String path = "serie";
        String url = urlBase.buildUrl(path);

        // Crea una solicitud POST
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                new Response.Listener <JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // La inserción fue exitosa
                        showAlert("Éxito", "Datos guardados correctamente en la API.");
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejo de errores en caso de fallo en la solicitud POST
                        error.printStackTrace();
                        if (error.networkResponse != null) {
                            String body;
                            // Obtiene el cuerpo de la respuesta
                            final String statusCode = String.valueOf(error.networkResponse.statusCode);
                            try {
                                body = new String(error.networkResponse.data, "UTF-8");
                                showAlert("Error de la API", "Error al guardar los datos en la API. Código de estado: " + statusCode + " Cuerpo: " + body);
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showAlert("Error de la API", "Error al guardar los datos en la API. Error: " + error.toString());
                        }
                    }
                }
        );

        // Añade la solicitud POST a la cola
        queue.add(postRequest);

    }

    private void showAlert(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(StartingRoutineActivity.this,
                R.style.AlertDialogTheme);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        builder.create().dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
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

    // Función para mostrar un mensaje
    private void showMessage(ImageButton typeSerie) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_type_serie, null);
        builder.setView(dialogView);

        AlertDialog dialog = builder.create();

        // Obtener las vistas del cuadro de diálogo
        LinearLayout warmupSeries = dialogView.findViewById(R.id.warmup_series);
        LinearLayout normalSeries = dialogView.findViewById(R.id.normal_series);
        LinearLayout descendingSeries = dialogView.findViewById(R.id.descending_series);
        LinearLayout failureSeries = dialogView.findViewById(R.id.failure_series);

        // Configurar los clics en las opciones
        warmupSeries.setOnClickListener(v -> {
            typeSerie.setImageResource(R.drawable.ic_warmup_serie);
            Toast.makeText(this, "Serie de calentamiento seleccionada", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        LinearLayout effectiveSeries = dialogView.findViewById(R.id.effective_series);
        effectiveSeries.setOnClickListener(v -> {
            typeSerie.setImageResource(R.drawable.green_check);
            Toast.makeText(this, "Serie efectiva seleccionada", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        normalSeries.setOnClickListener(v -> {
            typeSerie.setImageResource(R.drawable.ic_normal_serie);
            Toast.makeText(this, "Serie normal seleccionada", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        descendingSeries.setOnClickListener(v -> {
            typeSerie.setImageResource(R.drawable.ic_descending_serie);
            Toast.makeText(this, "Serie descendente seleccionada", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

        failureSeries.setOnClickListener(v -> {
            typeSerie.setImageResource(R.drawable.ic_failure_serie);
            Toast.makeText(this, "Serie al fallo seleccionada", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });

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
                EditText etPesSerie = findViewById(R.id.etPesSerie);
                EditText etRepsSerie = findViewById(R.id.etRepsSerie);
                TextView textViewExerciseName = findViewById(R.id.tvExerciseName);

                String peso = etPesSerie.getText().toString();
                String reps = etRepsSerie.getText().toString();
                String ejercicioNombre = textViewExerciseName.getText().toString();

                if (peso.isEmpty() || reps.isEmpty()) {
                    Toast.makeText(StartingRoutineActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                obtenerEjercicioId(ejercicioNombre, peso, reps, rutinaID);

                int newColor = ContextCompat.getColor(StartingRoutineActivity.this, R.color.green);
                newRow.setBackgroundColor(newColor);
            }
        });

        ImageButton typeSerie = newRow.findViewById(R.id.typeSerie);
        typeSerie.setOnClickListener(v -> showMessage(typeSerie));

        // Agregar la nueva fila al contenedor
        seriesContainer.addView(newRow);
    }
    public void endRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to end this training session?");
        builder.setPositiveButton("Yes", (dialog, which) -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - trainingDuration.getBase();
            String tiempoTardado = formatElapsedTime(elapsedMillis);
            obtenerRutinaName(rutinaId, () -> {
                insertRegistro(routineName, tiempoTardado);
                trainingDuration.stop();
                finish();
            });
        });
        builder.setNegativeButton("No", (dialog, which) -> dialog.dismiss());
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
        String path = "registros";
        String url = urlBase.buildUrl(path);

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

    private void obtenerRutinaName(int rutinaId, Runnable callback) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "rutinas/" + rutinaId;
        String url = urlBase.buildUrl(path);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        routineName = response.getString("nombre");
                        callback.run();  // Execute callback after name is fetched
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(StartingRoutineActivity.this, "Error al obtener el nombre de la rutina.", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> {
                    // Log error or notify user
                    Toast.makeText(StartingRoutineActivity.this, "Error al obtener el nombre de la rutina.", Toast.LENGTH_SHORT).show();
                });
        queue.add(jsonObjectRequest);
    }

}
