package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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
    private Chronometer trainingDuration;
    private Button endTraining;
    private LinearLayout routinesLayout;
    private long startTime;
    private int rutinaId;
    private String rutinaID;
    private String routineName;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_starting_routine);

        trainingDuration = findViewById(R.id.chronometer);
        endTraining = findViewById(R.id.btnEndTraining);
        routinesLayout = findViewById(R.id.exerciseTrainContainer);

        rutinaId = Integer.parseInt(getIntent().getStringExtra("ROUTINE_ID"));
        obtenerEjerciciosPorRutinaId(rutinaId);

        endTraining.setOnClickListener(v -> endRoutine());

        trainingDuration.setOnChronometerTickListener(chronometer -> {
            long elapsedMillis = SystemClock.elapsedRealtime() - chronometer.getBase();
            int minutes = (int) (elapsedMillis / 60000);
            int seconds = (int) ((elapsedMillis % 60000) / 1000);
            chronometer.setText(String.format("%02dmin %02ds", minutes, seconds));
        });

        trainingDuration.setBase(SystemClock.elapsedRealtime());
        trainingDuration.start();
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
                    EditText etPesSerie = seriesContainer.findViewById(R.id.etPesSerie);
                    EditText etRepsSerie = seriesContainer.findViewById(R.id.etRepsSerie);
                    EditText etCommentSerie = seriesContainer.findViewById(R.id.etCommentSerie);
                    String peso = etPesSerie.getText().toString();
                    String reps = etRepsSerie.getText().toString();
                    String comentario = etCommentSerie.getText().toString();

                    if (peso.isEmpty() || reps.isEmpty()) {
                        Toast.makeText(StartingRoutineActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                        return;
                    }
                    obtenerEjercicioId(ejercicioNombre, peso, reps, rutinaID, comentario);

                    // Visual feedback for first series row
                    LinearLayout firstRow = (LinearLayout) seriesContainer.getChildAt(0);
                    int newColor = ContextCompat.getColor(StartingRoutineActivity.this, R.color.green);
                    firstRow.setBackgroundColor(newColor);
                });

                // Configurar botón para eliminar ejercicio
                ImageButton eliminateExercise = exerciseView.findViewById(R.id.eliminate_cross);
                eliminateExercise.setOnClickListener(v -> deleteExercise(exerciseView));

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
        String getUrl = "http://172.17.176.1:8080/api/ejercicios/rutina/" + rutinaID;

        JsonArrayRequest getRequest = new JsonArrayRequest(Request.Method.GET, getUrl, null,
                response -> updateUIWithExercises(response),
                error -> {
                    error.printStackTrace();
                    Toast.makeText(this, "Error al obtener ejercicios", Toast.LENGTH_SHORT).show();
                }
        );

        queue.add(getRequest);
    }

    private void obtenerEjercicioId(String ejercicioNombre, String peso, String reps, String rutinaID, String comentario) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String getUrl = "http://172.17.176.1:8080/api/ejercicios/getEjercicioID?nom=" + ejercicioNombre + "&rutinaID=" + rutinaID;

        // Realiza la solicitud GET para obtener el ejercicioid
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, getUrl, null,
                new Response.Listener <JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            // Obtener el ejercicioid del objeto JSON de respuesta
                            String ejercicioid = response.getString("ejercicioID");
                            // Llamar a la función para guardar la serie con el ejercicioid obtenido
                            guardarSerieConEjercicioId(ejercicioid, peso, reps, comentario);
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

    private void guardarSerieConEjercicioId(String ejercicioid, String peso, String reps, String comentario) {
        // Construye el cuerpo de la solicitud en formato JSON con el ejercicioid
        RequestQueue queue = Volley.newRequestQueue(this);
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("peso", peso);
            jsonBody.put("n_repeticiones", reps);
            jsonBody.put("ejercicioID", ejercicioid);
            jsonBody.put("comentario_serie", comentario);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL para la solicitud POST
        String postUrl = "http://172.17.176.1:8080/api/serie";

        // Crea una solicitud POST
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST, postUrl, jsonBody,
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
                EditText etCommentSerie = findViewById(R.id.etCommentSerie);
                TextView textViewExerciseName = findViewById(R.id.tvExerciseName);

                String peso = etPesSerie.getText().toString();
                String reps = etRepsSerie.getText().toString();
                String comentario = etCommentSerie.getText().toString();
                String ejercicioNombre = textViewExerciseName.getText().toString();
                String rutinaID = "6";

                if (peso.isEmpty() || reps.isEmpty()) {
                    Toast.makeText(StartingRoutineActivity.this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show();
                    return;
                }
                obtenerEjercicioId(ejercicioNombre, peso, reps, rutinaID, comentario);

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
        String url = "http://172.17.176.1:8080/api/registros";
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
        String url = "http://172.17.176.1:8080/api/rutinas/" + rutinaId;

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
