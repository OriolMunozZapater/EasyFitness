package com.uablis.easyfitness;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class RecommendedActivitiesActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_activities);

        int userID = 6; // Reemplaza esto con el ID del usuario real
        obtenerSugerencias(userID);

        // Configurar el botón de regreso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void obtenerSugerencias(int userID) {

        String path = "ejercicios/sugerencias/" + userID;
        String url = urlBase.buildUrl(path);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<JSONObject> ejercicios = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject ejercicio = response.getJSONObject(i);
                                ejercicios.add(ejercicio);
                                Log.d("Ejercicio", "Nombre: " + ejercicio.getString("nombre") + ", Grupo Muscular: " + ejercicio.getString("grupoMuscular"));
                            }
                            mostrarSugerencias(ejercicios);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        );

        RequestQueue queue = Volley.newRequestQueue(this);
        queue.add(request);
    }

    public void mostrarSugerencias(List<JSONObject> ejercicios) {
        LinearLayout container = findViewById(R.id.usersContainer);
        container.removeAllViews();

        for (JSONObject ejercicio : ejercicios) {
            View view = getLayoutInflater().inflate(R.layout.row_recommended_activities, null);
            TextView exerciseName = view.findViewById(R.id.exerciseName);
            TextView bodyPart = view.findViewById(R.id.bodyPart);

            try {
                exerciseName.setText(ejercicio.getString("nombre"));
                bodyPart.setText(ejercicio.getString("grupoMuscular"));
            } catch (JSONException e) {
                e.printStackTrace();
            }

            container.addView(view);
        }
    }
}
