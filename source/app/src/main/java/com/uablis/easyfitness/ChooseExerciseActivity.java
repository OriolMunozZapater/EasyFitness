package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.search.SearchBar;
import com.google.firebase.crashlytics.buildtools.reloc.org.apache.http.HttpStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ChooseExerciseActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private SearchBar exerciseSearch;
    LinearLayout exerciseContainer;
    private Button selectMuscle, newExercise;
    private ImageView backArrow;
    private SharedPreferences tempExerciseIDs;
    private List<Integer> currentExerciseIds = new ArrayList<>();
    private Integer userID=Integer.parseInt(UsuarioActual.getInstance().getUserId());
    private static final int ADD_EXERCISE_REQUEST = 1; // Constante de código de solicitud

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_exercise);

        //fer la logica de busca del searchbar
        exerciseSearch = findViewById(R.id.exerciseSearch);
        selectMuscle = findViewById(R.id.selectMuscle);
        backArrow = findViewById(R.id.back_arrow);
        newExercise = findViewById(R.id.btnNewExercise);

        tempExerciseIDs = getSharedPreferences("ArrayExerciseIDs", MODE_PRIVATE);
        loadArray(); //Inicializar lista con ID de BD

        //loadEjercicios(0); //TODO
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
        routinesLayout.removeAllViews();

        loadEjercicios(0); //TODO
        loadEjercicios(userID);

        newExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToNewExercise(userID);

            }
        });
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backScreen();
            }
        });

        selectMuscle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectMuscle(userID);
            }
        });
    }

    private void loadEjercicios(int userID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "ejercicios/user/" + userID;
        String url = urlBase.buildUrl(path);

        //String url = "http://10.109.31.137:8080/api/ejercicios/user/" + userID;


        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<String> exerciseNameList = new ArrayList<>();
                            List<Integer> exerciseIDList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int exerciseId = Integer.parseInt(jsonObject.getString("ejercicioID"));

                                if (!currentExerciseIds.contains(exerciseId)) {
                                    exerciseNameList.add(jsonObject.getString("nombre"));
                                    exerciseIDList.add(exerciseId);
                                }
                            }

                            // Convertir listas a arrays si es necesario para la función updateUIWithExercises
                            String[] exerciseNames = exerciseNameList.toArray(new String[0]);
                            Integer[] exerciseIDs = exerciseIDList.toArray(new Integer[0]);

                            updateUIWithExercices(exerciseNames, exerciseIDs);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChooseExerciseActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    // No se encontraron ejercicios relacionados con ese músculo
                    Toast.makeText(ChooseExerciseActivity.this, "No se encontraron ejercicios para usuario: "+userID, Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(ChooseExerciseActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }

    private void loadWithMuscle(int userID, String selectedMuscle) {
        RequestQueue queue = Volley.newRequestQueue(this);

        String path = "ejercicios/user/" + userID + "/muscle/" + selectedMuscle;
        String url = urlBase.buildUrl(path);
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
        routinesLayout.removeAllViews();

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            List<String> exerciseNameList = new ArrayList<>();
                            List<Integer> exerciseIDList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int exerciseId = Integer.parseInt(jsonObject.getString("ejercicioID"));

                                if (!currentExerciseIds.contains(exerciseId)) {
                                    exerciseNameList.add(jsonObject.getString("nombre"));
                                    exerciseIDList.add(exerciseId);
                                }
                            }

                            // Convertir listas a arrays si es necesario para la función updateUIWithExercises
                            String[] exerciseNames = exerciseNameList.toArray(new String[0]);
                            Integer[] exerciseIDs = exerciseIDList.toArray(new Integer[0]);
                            updateUIWithExercices(exerciseNames,exerciseIDs);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChooseExerciseActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {
                    // No se encontraron ejercicios relacionados con ese músculo
                    Toast.makeText(ChooseExerciseActivity.this, "No se encontraron ejercicios para usuario: "+userID, Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(ChooseExerciseActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }


    //TODO CAMBIAR DUPLICAR CODIGO
    private void updateUIWithExercices(String[] exerciseNames, Integer[] exerciseIDs) {
        //Visualizacion en la pantalla
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);

        for (int pos=0; pos<exerciseNames.length;pos++) {
            View exerciseView = getLayoutInflater().inflate(R.layout.exercise_row2, routinesLayout, false);
            TextView textView = exerciseView.findViewById(R.id.tvExerciseName);

            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) ImageButton addToRoutine = exerciseView.findViewById(R.id.addToRoutine);

            textView.setText(exerciseNames[pos]);
            int finalPos = pos;
            addToRoutine.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ImageButton button = (ImageButton) v;
                    button.setImageResource(R.drawable.green_check);
                    afegirExercisiPreference(exerciseIDs[finalPos]);
                    currentExerciseIds.add(exerciseIDs[finalPos]);
                }
            });
            exerciseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            routinesLayout.addView(exerciseView);
        }
    }

    public void goToNewExercise(int userID) {
        Intent intent = new Intent(ChooseExerciseActivity.this, CreateNewExercise.class);
        startActivityForResult(intent, ADD_EXERCISE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == ADD_EXERCISE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Aquí puedes hacer lo que necesitas después de volver de ChooseExerciseActivity
                LinearLayout exerciseLayout = findViewById(R.id.exerciseContainer);
                exerciseLayout.removeAllViews();
                loadEjercicios(0);
                loadEjercicios(userID);
            }
        }
    }

    public void backScreen() {
        Intent returnIntent = new Intent();
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    public void selectMuscle(int userID) {
        // Obtenemos los nombres de los músculos desde los recursos de strings
        final String[] muscleNames = getResources().getStringArray(R.array.muscle_options);

        // Creamos un AlertDialog con la lista de músculos
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona un músculo")
                .setItems(muscleNames, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Acción a realizar cuando se selecciona un músculo
                        String selectedMuscle = muscleNames[which];
                        // Puedes hacer algo con el músculo seleccionado, como mostrar un Toast
                        Toast.makeText(getApplicationContext(), "Has seleccionado: " + selectedMuscle, Toast.LENGTH_SHORT).show();
                        selectMuscle.setText(selectedMuscle);
                        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
                        routinesLayout.removeAllViews();

                        if(selectedMuscle.equals("All")){
                            loadEjercicios(0);
                            loadEjercicios(userID);
                        }
                        else{
                            loadWithMuscle(0, selectedMuscle);
                            loadWithMuscle(userID, selectedMuscle);
                        }
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    public void afegirExercisiPreference(int exerciseID){
        // Obtener la cadena existente de IDs de SharedPreferences con el nuevo identificador
        String existingIds = tempExerciseIDs.getString("selectedExerciseIds", "");
        String newIds;

        // Comprobar si ya existen IDs guardados
        if (!existingIds.isEmpty()) {
            newIds = existingIds + "," + exerciseID; // Añadir el nuevo ID a la cadena existente
        } else {
            newIds = Integer.toString(exerciseID); // No había IDs antes, iniciar la cadena con el nuevo ID
        }

        // Guardar la nueva cadena de IDs en SharedPreferences usando el nuevo identificador
        SharedPreferences.Editor editor = tempExerciseIDs.edit();
        editor.putString("selectedExerciseIds", newIds);
        editor.apply(); // Guardar los cambios de forma asincrónica
    }

    private void loadArray() {

        SharedPreferences prefs = getSharedPreferences("ArrayExerciseIDs", MODE_PRIVATE);
        String idsBD = prefs.getString("exerciseIdsBD", "");
        String selectedIds = prefs.getString("selectedExerciseIds", "");
        String idsString = idsBD + (idsBD.isEmpty() || selectedIds.isEmpty() ? "" : ",") + selectedIds;

        currentExerciseIds.clear();

        // Convertir la cadena de IDs a la lista
        if (!idsString.isEmpty()) {
            String[] idsArray = idsString.split(",");
            for (String idStr : idsArray) {
                try {
                    int id = Integer.parseInt(idStr);
                    currentExerciseIds.add(id);
                } catch (NumberFormatException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}