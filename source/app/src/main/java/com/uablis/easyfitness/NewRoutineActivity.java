package com.uablis.easyfitness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NewRoutineActivity extends AppCompatActivity {

    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private ImageButton eliminateCross;
    private ImageView home, trainingRoutinesButton, profile, training_session;
    LinearLayout exerciseContainer;
    private EditText etEditRoutineName;
    private ImageView backArrow;
    private Button btnAddExercise, btnSaveRoutine;
    private List<Integer> currentExerciseIds = new ArrayList<>();
    private SharedPreferences tempExerciseIDs;
    private static final int ADD_EXERCISE_REQUEST = 1; // Constante de código de solicitud

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_routine);

        backArrow = findViewById(R.id.back_arrow);
        btnAddExercise = findViewById(R.id.btnAddExercise1);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);
        btnSaveRoutine = findViewById(R.id.btnSaveNewRoutine);
        etEditRoutineName=findViewById(R.id.etRoutineName);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRoutineActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(NewRoutineActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(NewRoutineActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(NewRoutineActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        tempExerciseIDs = getSharedPreferences("ArrayExerciseIDs", MODE_PRIVATE);
        SharedPreferences.Editor editor = tempExerciseIDs.edit();
        // Elimina todos los datos
        editor.clear();
        // Guarda los cambios
        editor.apply();
        loadExercisesID();

        btnSaveRoutine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveRoutine();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToScreen();
            }
        });

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToAddExerciseScreen();
            }
        });
    }

    private void loadExercisesID() {
        List<Integer> loadedIds = loadExerciseIdsFromPrefs(); // Carga los IDs de SharedPreferences
        currentExerciseIds.addAll(loadedIds);

        //Cargar los ejercicios con los ID obtenidos anteriormente
        RequestQueue queue = Volley.newRequestQueue(this);
        String ids = TextUtils.join(",", currentExerciseIds); // Convertir el array a una cadena separada por comas


        String path = "ejercicios/name/" + ids;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String[] exerciseNames = new String[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                exerciseNames[i] = jsonObject.getString("nombre");
                            }

                            updateUIWithExercises(exerciseNames, currentExerciseIds.toArray(new Integer[0]));
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NewRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    // No se encontraron ejercicios relacionados con ese músculo
                    Toast.makeText(NewRoutineActivity.this, "No se encontraron ejercicios en la rutina", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(NewRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }
    private List<Integer> loadExerciseIdsFromPrefs() {
        SharedPreferences prefs = getSharedPreferences("ArrayExerciseIDs", MODE_PRIVATE);
        String idsString = prefs.getString("selectedExerciseIds", "");
        List<Integer> exerciseIds = new ArrayList<>();
        if (!idsString.isEmpty()) {
            for (String id : idsString.split(",")) {
                try {
                    exerciseIds.add(Integer.parseInt(id));
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
        return exerciseIds;
    }

    private void updateUIWithExercises(String[] exerciseNames, Integer[] exerciseID) {
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
        int pos=0;

        //View routineView = getLayoutInflater().inflate(R.layout.activity_new_routine, routinesLayout, false);
        //TextView textViewRoutine = routineView.findViewById(R.id.etEditRoutineName);

        //etEditRoutineName=findViewById(R.id.etEditRoutineName);

        for (String name : exerciseNames) {

            View exerciseView = getLayoutInflater().inflate(R.layout.exercise_row, routinesLayout, false);
            TextView textView = exerciseView.findViewById(R.id.tvExerciseName);
            ImageButton eliminateCross = exerciseView.findViewById(R.id.eliminate_cross);

            textView.setText(name);

            exerciseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEditExercise();
                }
            });

            int finalPos = pos;
            eliminateCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteExercise(exerciseID[finalPos]);
                }
            });
            routinesLayout.addView(exerciseView);
            pos++;
        }
    }

    public void saveRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to create a new routine?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                addRoutine();
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
                showAddingRoutinePopup();
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

    public void goToRoutines() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();

    }

    public void goToEditExercise(){
        Intent intent = new Intent(NewRoutineActivity.this, EditExerciseActivity.class);
        startActivity(intent);
    }
    public void backToScreen() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void deleteExercise(Integer exerciseID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de eliminar?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        SharedPreferences prefs = getSharedPreferences("ArrayExerciseIDs", MODE_PRIVATE);
                        String idsString = prefs.getString("selectedExerciseIds", "");
                        List<Integer> idsList = new ArrayList<>();

                        // Convertir la cadena a una lista
                        if (!idsString.isEmpty()) {
                            for (String idStr : idsString.split(",")) {
                                try {
                                    idsList.add(Integer.parseInt(idStr));
                                } catch (NumberFormatException e) {
                                    Log.e("RemoveID", "Error parsing ID: " + idStr, e);
                                }
                            }
                        }

                        Toast.makeText(NewRoutineActivity.this, "Ejercicio eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        //RELOAD pantalla
                        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
                        routinesLayout.removeAllViews();

                        currentExerciseIds.remove(exerciseID);
                        removeExerciseIdFromPrefs(exerciseID, idsList);

                        loadExercisesID();

                    }

                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Acción a realizar si se elige "No"
                        dialog.cancel(); // Cierra la alerta
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    public void goToAddExerciseScreen() {
        Intent intent = new Intent(NewRoutineActivity.this, ChooseExerciseActivity.class);
        startActivityForResult(intent, ADD_EXERCISE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EXERCISE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Aquí puedes hacer lo que necesitas después de volver de ChooseExerciseActivity
                LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
                routinesLayout.removeAllViews();
                currentExerciseIds.clear();
                loadExercisesID();
            }
        }
    }

    public void addRoutine(){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nombre", etEditRoutineName.getText().toString().trim());
            jsonBody.put("userID", Integer.parseInt(UsuarioActual.getInstance().getUserId()));
            jsonBody.put("publico", false);
            jsonBody.put("descripcion", "hola");

        } catch (JSONException e) {
            e.printStackTrace();
        }
        sendUpdateRequestRutina(jsonBody);
        //esperar a que se haga el insert en la BBDD


    }

    public void getRutinaID(){
        RequestQueue queue = Volley.newRequestQueue(this);
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        //  String url = "http://192.168.100.1:8080/api/rutinas/last/" + userID;
        String path = "rutinas/last/" + userID;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        int rutinaID = Integer.parseInt(response);

                        for(int ejercicioID : currentExerciseIds) {
                            saveExercisesToDatabase(rutinaID, ejercicioID);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(NewRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }
    public void removeExerciseIdFromPrefs(int idToRemove,  List<Integer> idsList) {

        SharedPreferences prefs = getSharedPreferences("ArrayExerciseIDs", MODE_PRIVATE);
        // Remover el ID deseado
        idsList.remove((Integer) idToRemove);  // Usa (Integer) para asegurarte de que eliminas por objeto, no por índice

        // Convertir la lista de vuelta a una cadena
        StringBuilder newIdsString = new StringBuilder();
        for (int i = 0; i < idsList.size(); i++) {
            newIdsString.append(i > 0 ? "," : "").append(idsList.get(i));
        }

        // Guardar la cadena actualizada en SharedPreferences
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("selectedExerciseIds", newIdsString.toString());
        editor.apply();
    }


    public void saveExercisesToDatabase(Integer rutinaID, Integer ejercicioID) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("rutinaId", rutinaID);
            jsonBody.put("ejercicioId", ejercicioID);
            jsonBody.put("orden",3);
            //TODO METER ORDEN
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        sendUpdateRequest(jsonBody);
    }

    private void sendUpdateRequest(JSONObject requestBody) {
        //CAMBIAR

        //String url = "http://192.168.100.1:8080/api/ejercicio/";
        String path = "rutina_ejercicios";
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
                        showAddingRoutinePopup();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Ocurrió un error al hacer la solicitud
                        error.printStackTrace();
                        Log.e("VolleyError", "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response Data: " + new String(error.networkResponse.data));
                        }
                        //Toast.makeText(CreateNewExercise.this, "Error al crear el ejercicio: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
        ) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    private void sendUpdateRequestRutina(JSONObject requestBody) {
        //CAMBIAR

        //String url = "http://192.168.100.1:8080/api/ejercicio/";
        String path = "rutinas";
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(NewRoutineActivity.this, "Rutina creada correctamente", Toast.LENGTH_SHORT).show();
                        getRutinaID();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Ocurrió un error al hacer la solicitud
                        error.printStackTrace();
                        Log.e("VolleyError", "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response Data: " + new String(error.networkResponse.data));
                        }
                        //Toast.makeText(CreateNewExercise.this, "Error al crear el ejercicio: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
        ) {
            @Override
            public byte[] getBody() {
                String jsonString = requestBody.toString();
                Log.d("RequestBody", jsonString); // Agrega este log
                return jsonString.getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(stringRequest);
    }

    private void showAddingRoutinePopup() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_routine);

        android.app.AlertDialog dialog = builder.create();
        dialog.show();

        // Cerrar el diálogo después de 3 segundos (3000 milisegundos)
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            finish();
        }, 3000);
    }
}