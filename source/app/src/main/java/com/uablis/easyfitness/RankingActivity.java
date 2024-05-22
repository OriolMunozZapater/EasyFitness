package com.uablis.easyfitness;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

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
import java.util.List;

public class RankingActivity extends AppCompatActivity {

    private class Exercise {
        private int id;
        private int positive;
        private int negative;

        public Exercise(int id, boolean firstPuntuation) {
            this.id = id;
            if(firstPuntuation){
                this.positive = 1;
                this.negative = 0;
            }else{
                this.positive = 0;
                this.negative = 1;
            }

        }

        public int getId() {
            return id;
        }

        public int getPositive() {
            return positive;
        }

        public void setPositive(int positive) {
            this.positive = positive;
        }

        public int getNegative() {
            return negative;
        }

        public void setNegative(int negative) {
            this.negative = negative;
        }

        public double calculateScore() {
            double penaltyFactor = 1;
            int totalVotes = positive + negative;
            if (totalVotes == 0) {
                return 0; // Return 0 if there are no votes
            }
            double adjustedPositive = positive - penaltyFactor * negative;
            if (adjustedPositive < 0) {
                adjustedPositive = 0; // Ensure the adjusted positive count is not negative
            }
            double score = (adjustedPositive / totalVotes) * 5;
            return score;
        }


    }


    ApiUrlBuilder urlBase = new ApiUrlBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ranking);

        getInfoFromDB();

    }

    private void getInfoFromDB(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "valoracion_ejercicio";
        String url = urlBase.buildUrl(path);



        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);


                            List<Exercise> exerciseList = new ArrayList<>();

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int exerciseID = jsonObject.isNull("ejercicioID") ? 0 : Integer.parseInt(jsonObject.getString("ejercicioID"));
                                Boolean exercisePuntuation = jsonObject.isNull("valoracion") ? true : Boolean.parseBoolean(jsonObject.getString("valoracion"));

                                // Buscar si el ejercicio ya está en la lista
                                boolean exerciseExists = false;
                                for (Exercise exercise : exerciseList) {
                                    if (exercise.getId() == exerciseID) {
                                        exerciseExists = true;
                                        // Si la puntuación es mayor, actualizarla
                                        if (exercisePuntuation) {
                                            exercise.setPositive(exercise.getPositive()+1);
                                        }else{
                                            exercise.setNegative(exercise.getNegative()+1);
                                        }
                                        break;
                                    }
                                }

                                // Si el ejercicio no está en la lista, añadirlo
                                if (!exerciseExists) {
                                    exerciseList.add(new Exercise(exerciseID, exercisePuntuation));
                                }
                            }

                            // Actualizar la interfaz de usuario con la lista de ejercicios
                            for (Exercise exercise : exerciseList) {

                                getExerciseNamesFromDB(exercise.getId(), exercise.calculateScore());
                            }



                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(RankingActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                if (error.networkResponse != null && error.networkResponse.statusCode == HttpStatus.SC_NOT_FOUND) {

                    Toast.makeText(RankingActivity.this, "Error cargando ranking", Toast.LENGTH_SHORT).show();
                } else {
                    // Error al hacer la llamada al servidor
                    Toast.makeText(RankingActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                }
            }
        });
        queue.add(stringRequest);
    }

    private void getExerciseNamesFromDB(int id, double Puntuation) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "ejercicios/" + id;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        String exerciseName = jsonObject.getString("nombre");
                        updateUIWithExercises(exerciseName, Puntuation);
                    } catch (JSONException e) {
                        Toast.makeText(RankingActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                },
                error -> Toast.makeText(RankingActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show()
        );

        queue.add(stringRequest);
    }


    private void updateUIWithExercises(String exerciseName, Double exercisePuntuation)  {

        LinearLayout ratingLayout = findViewById(R.id.valoratingContainer);

        View routineView = getLayoutInflater().inflate(R.layout.valoration_exercise_row, ratingLayout, false);
        TextView textView = routineView.findViewById(R.id.tvExerciseName);
        RatingBar rating_bar_exercise = routineView.findViewById(R.id.exExerciseRate);


        textView.setText(exerciseName); // Usar la copia final de 'i'

        rating_bar_exercise.setRating((float) exercisePuntuation.floatValue()); // Valoración de ejemplo


        ratingLayout.addView(routineView);
    }


}