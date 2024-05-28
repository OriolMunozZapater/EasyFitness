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
import android.widget.Toast;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

public class EditRoutineActivity extends AppCompatActivity {


    private List<Integer> currentExerciseIds = new ArrayList<>();
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private ImageView home, trainingRoutinesButton, profile, training_session;
    LinearLayout exerciseContainer;
    private EditText etEditRoutineName;
    private ImageView backArrow;
    private Button btnAddExercise, btnSave;
    private Integer rutinaID;
    private SharedPreferences tempExerciseIDs;
    private static final int ADD_EXERCISE_REQUEST = 1; // Constante de código de solicitud

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);

        backArrow = findViewById(R.id.back_arrow);
        btnAddExercise = findViewById(R.id.btnAddExercise1);
        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);
        etEditRoutineName = findViewById(R.id.etEditRoutineName);
        btnSave = findViewById(R.id.btnSave);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditRoutineActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditRoutineActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(EditRoutineActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(EditRoutineActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        rutinaID=getIntent().getIntExtra("ROUTINE_ID", 0); // 0 es un valor predeterminado en caso de no encontrarse el dato.


        tempExerciseIDs = getSharedPreferences("ArrayExerciseIDs", MODE_PRIVATE);
        SharedPreferences.Editor editor = tempExerciseIDs.edit();
        // Elimina todos los datos
        editor.clear();
        // Guarda los cambios
        editor.apply();
        loadRoutineName();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
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

    private void updateUIWithExercises(String[] exerciseNames, Integer[] exerciseID) {
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
        int pos=0;

        for (String name : exerciseNames) {

            View exerciseView = getLayoutInflater().inflate(R.layout.exercise_row, routinesLayout, false);
            TextView textView = exerciseView.findViewById(R.id.tvExerciseName);
            ImageButton eliminateCross = exerciseView.findViewById(R.id.eliminate_cross);

            textView.setText(name);

            // exerciseView.setOnClickListener(new View.OnClickListener() {
            //    @Override
            //    public void onClick(View v) {
            //        goToEditExercise();
            //    }
            // });

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

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to modificate this routine?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                saveExercisesToDatabase();
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
        Intent intent = new Intent(EditRoutineActivity.this, TrainingRoutinesActivity.class);
        startActivity(intent);
    }

    public void goToEditExercise(){
        Intent intent = new Intent(EditRoutineActivity.this, EditExerciseActivity.class);
        startActivity(intent);
    }
    public void backToScreen() {
        finish();
    }

    private void loadRoutineName(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "rutinas/" + rutinaID;
        String url = urlBase.buildUrl(path);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            String name=jsonObject.getString("nombre");
                            etEditRoutineName.setText(name);

                            loadUserExercisesID();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    // No se encontraron ejercicios relacionados con ese músculo
                    Toast.makeText(EditRoutineActivity.this, "No se encontraron ejercicios en la rutina", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(EditRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);

    }
    private void loadUserExercisesID() {
        //Obtener los ID de los ejercicios que pertenecen a una rutina
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "rutina_ejercicios/rutina/" + rutinaID;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            currentExerciseIds.clear();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                currentExerciseIds.add(jsonObject.getInt("ejercicioId"));
                            }

                            //Guardar la cadena en SharedPreferences
                            SharedPreferences.Editor editor = tempExerciseIDs.edit(); // Usar la variable de instancia
                            String idsString = TextUtils.join(",", currentExerciseIds);
                            editor.putString("exerciseIdsBD", idsString);
                            editor.apply();

                            loadUserExercisesName();

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    // No se encontraron ejercicios relacionados con ese músculo
                    Toast.makeText(EditRoutineActivity.this, "No se encontraron ejercicios en la rutina", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(EditRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }

    private void loadUserExercisesName() {

        List<Integer> loadedIds = loadExerciseIdsFromPrefs(); // Carga los IDs de SharedPreferences
        currentExerciseIds.addAll(loadedIds);

        //Cargar los ejercicios con los ID obtenidos anteriormente
        RequestQueue queue = Volley.newRequestQueue(this);
        String ids = TextUtils.join(",", currentExerciseIds); // Convertir el array a una cadena separada por comas


        //String url = "http://10.109.31.137:8080/api/ejercicios/name/" + ids;
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
                            Toast.makeText(EditRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    // No se encontraron ejercicios relacionados con ese músculo
                    Toast.makeText(EditRoutineActivity.this, "No se encontraron ejercicios", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(EditRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }


    private boolean deleteExerciseFromDatabase(Integer exerciseID) {
        //String url="http://10.109.31.137:8080/api/rutina_ejercicios/delete/"+exerciseID+"/"+rutinaID;
        String path = "rutina_ejercicios/delete/"+exerciseID+"/"+rutinaID;
        String url = urlBase.buildUrl(path);
        // Utiliza un objeto AtomicBoolean para mantener el estado de deleted
        AtomicBoolean deleted = new AtomicBoolean(true);

        RequestQueue queue = Volley.newRequestQueue(this);

        // Crear la solicitud DELETE
        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Manejar cualquier error de la solicitud DELETE
                        error.printStackTrace();
                        deleted.set(false);
                    }
                });

        // Agregar la solicitud a la cola de solicitudes
        queue.add(stringRequest);
        return deleted.get();
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

                        Toast.makeText(EditRoutineActivity.this, "Ejercicio eliminado exitosamente", Toast.LENGTH_SHORT).show();
                        //RELOAD pantalla
                        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
                        routinesLayout.removeAllViews();

                        if(!idsList.contains(exerciseID)){
                            if(deleteExerciseFromDatabase(exerciseID)) {
                                try {
                                    Thread.sleep(150);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }else{
                                Toast.makeText(EditRoutineActivity.this, "Error al eliminar el ejercicio", Toast.LENGTH_SHORT).show();
                            }
                        } else{
                            currentExerciseIds.remove(exerciseID);
                            removeExerciseIdFromPrefs(exerciseID, idsList);
                        }

                        loadUserExercisesID();

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
        Intent intent = new Intent(EditRoutineActivity.this, ChooseExerciseActivity.class);
        startActivityForResult(intent, ADD_EXERCISE_REQUEST);
    }
    public void saveExercisesToDatabase() {
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


        for( int id:idsList){
            JSONObject jsonBody = new JSONObject();
            try {
                jsonBody.put("rutinaId", rutinaID);
                jsonBody.put("ejercicioId", id);
                jsonBody.put("orden",3); //TODO Eliminar
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            sendUpdateRequest(jsonBody);

        }
        sendUpdateRequestName();

    }

    private void sendUpdateRequest(JSONObject requestBody) {

        String path = "rutina_ejercicios";
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

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
    private void sendUpdateRequestName() {

        String path = "rutinas/actualizarNombre/"+rutinaID+"/"+etEditRoutineName.getText();
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.PUT, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(EditRoutineActivity.this, "Rutina modificada correctamente", Toast.LENGTH_SHORT).show();
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
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EXERCISE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Aquí puedes hacer lo que necesitas después de volver de ChooseExerciseActivity
                LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
                routinesLayout.removeAllViews();
                loadUserExercisesName();
            }
        }
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