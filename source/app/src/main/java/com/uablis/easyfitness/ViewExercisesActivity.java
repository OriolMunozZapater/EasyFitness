package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

public class ViewExercisesActivity extends AppCompatActivity {
    private LinearLayout exercisesContainer;
    private RequestQueue requestQueue;
    private ImageView home, trainingRoutinesButton, profile, training_session;
    private ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private ImageView backArrow;
    List<String> NameList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_exercise);

        backArrow = findViewById(R.id.back_arrow);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);
        loadInfoEjercicios();

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewExercisesActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewExercisesActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(ViewExercisesActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(ViewExercisesActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToScreen();
            }
        });
    }

    private void updateUIWithExercises(String[] exerciseNames, Integer[] exerciseIDs) {
        LinearLayout exercisesLayout = findViewById(R.id.exerciseContainer);
        int pos=0;

        for (String name : exerciseNames) {

            View exerciseView = getLayoutInflater().inflate(R.layout.exercise_row3, exercisesLayout, false);
            TextView textView = exerciseView.findViewById(R.id.tvExerciseName);
            ImageButton commentCross = exerciseView.findViewById(R.id.botonComentarioApp);

            textView.setText(name);

            int finalPos = pos;
            commentCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String userID;
                    goToAddComent(exerciseIDs[finalPos]);
                }
            });
            exercisesLayout.addView(exerciseView);
            pos++;
        }
    }

    private void loadInfoEjercicios() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "ejercicios/obtener";
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONArray jsonArray = new JSONArray(response);
                        List<String> exerciseNameList = new ArrayList<>();
                        List<Integer> exerciseIDList = new ArrayList<>();
                        List<Integer> exerciseUserID = new ArrayList<>();

                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                            exerciseNameList.add(jsonObject.getString("nombre"));
                            exerciseIDList.add(jsonObject.getInt("ejercicioID"));
                            exerciseUserID.add(jsonObject.getInt("userID"));
                        }

                        List<String> combinedNames = new ArrayList<>(Collections.nCopies(exerciseNameList.size(), null));
                        AtomicInteger counter = new AtomicInteger(exerciseUserID.size());

                        for (int i = 0; i < exerciseUserID.size(); i++) {
                            final int index = i;
                            loadUserIDNames(exerciseUserID.get(i), name -> {
                                if (name != null && !name.isEmpty())
                                {
                                    combinedNames.set(index, exerciseNameList.get(index) + " por " + name);
                                    if (counter.decrementAndGet() == 0) {
                                        updateUIWithExercises(combinedNames.toArray(new String[0]), exerciseIDList.toArray(new Integer[0]));
                                    }
                                }
                            });
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ViewExercisesActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(ViewExercisesActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }


    public void goToAddComent(Integer userID){
        Intent intent = new Intent(ViewExercisesActivity.this, AddComent.class);
        intent.putExtra("EJERCICIO_ID", userID);
        startActivity(intent);
    }

    public void loadUserIDNames(Integer userID, Consumer<String> callback) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "usuarios/buscar/"+userID;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            callback.accept(jsonObject.getString("nombre"));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                        Toast.makeText(ViewExercisesActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> {
            error.printStackTrace();
            Toast.makeText(ViewExercisesActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
        });

        queue.add(stringRequest);
    }


    public void goToEditExercise(){
        Intent intent = new Intent(ViewExercisesActivity.this, EditExerciseActivity.class); //TODO CAMBIAR REDIRECCION
        startActivity(intent);
    }
    public void backToScreen() {
        finish();
    }
    public void goToRoutines() {
        Intent intent = new Intent(ViewExercisesActivity.this, TrainingRoutinesActivity.class);
        startActivity(intent);
    }
}