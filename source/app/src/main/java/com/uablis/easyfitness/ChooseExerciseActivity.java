package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
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

public class ChooseExerciseActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private SearchBar exerciseSearch;
    LinearLayout exerciseContainer;
    private Button selectMuscle, newExercise;
    private ImageView backArrow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_exercise);

        //fer la logica de busca del searchbar
        exerciseSearch = findViewById(R.id.exerciseSearch);
        selectMuscle = findViewById(R.id.selectMuscle);
        backArrow = findViewById(R.id.back_arrow);
        newExercise = findViewById(R.id.btnNewExercise);

        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        //loadEjercicios(0);
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
                            String[] exerciseNames = new String[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                exerciseNames[i] = jsonObject.getString("nombre");
                            }

                            LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
                            routinesLayout.removeAllViews();
                            updateUIWithExercices(exerciseNames);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChooseExerciseActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ChooseExerciseActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void updateUIWithExercices(String[] exerciseNames) {
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);

        for (String name : exerciseNames) {
            View exerciseView = getLayoutInflater().inflate(R.layout.exercise_row2, routinesLayout, false);
            TextView textView = exerciseView.findViewById(R.id.tvExerciseName);


            textView.setText(name);
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
        startActivity(intent);
        LinearLayout exerciseLayout = findViewById(R.id.exerciseContainer);
        exerciseLayout.removeAllViews();
        //loadEjercicios(0);
        loadEjercicios(userID);
    }

    public void backScreen() {
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
                        if(selectedMuscle.equals("All"))
                            loadEjercicios(userID);
                        else
                            loadWithMuscle(userID, selectedMuscle);
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadWithMuscle(int userID, String selectedMuscle) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //String url = "http://192.168.100.1:8080/api/ejercicios/user/" + userID + "/" + selectedMuscle;
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
                            String[] exerciseNames = new String[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                exerciseNames[i] = jsonObject.getString("nombre");
                            }
                            updateUIWithExercices(exerciseNames);

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
                    Toast.makeText(ChooseExerciseActivity.this, "No se encontraron ejercicios", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(ChooseExerciseActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }
}