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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RecommendedActivitiesActivity extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_activities);

        int userID = 4; // Reemplaza esto con el ID del usuario real
        obtenerRegistrosUser(userID);

        // Configurar el botón de regreso
        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    public void obtenerRegistrosUser(int userID) {
        String path = "registros/user/" + userID;
        String url = urlBase.buildUrl(path);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<JSONObject> registrosUsuario = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject registro = response.getJSONObject(i);
                                registrosUsuario.add(registro);
                                Log.d("RegistroUsuario", "Nombre Rutina: " + registro.getString("nombreRutina"));
                            }
                            // Obtener todas las rutinas disponibles
                            obtenerTodasRutinas(registrosUsuario);
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

    public void obtenerTodasRutinas(List<JSONObject> registrosUsuario) {
        String path = "rutinas";
        String url = urlBase.buildUrl(path);

        JsonArrayRequest request = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        List<String> todasRutinas = new ArrayList<>();
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject rutina = response.getJSONObject(i);
                                todasRutinas.add(rutina.getString("nombre"));
                                Log.d("Rutina", "Nombre: " + rutina.getString("nombre"));
                            }
                            // Comparar y obtener rutinas menos hechas
                            List<String> rutinasMenosHechas = obtenerRutinasMenosHechas(registrosUsuario, todasRutinas);
                            mostrarSugerencias(rutinasMenosHechas);
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

    public List<String> obtenerRutinasMenosHechas(List<JSONObject> registrosUsuario, List<String> todasRutinas) {
        Map<String, Integer> rutinaContador = new HashMap<>();
        try {
            for (String rutina : todasRutinas) {
                rutinaContador.put(rutina, 0);
            }

            for (JSONObject registro : registrosUsuario) {
                String nombreRutina = registro.getString("nombreRutina");
                if (rutinaContador.containsKey(nombreRutina)) {
                    rutinaContador.put(nombreRutina, rutinaContador.get(nombreRutina) + 1);
                }
            }

            int minHechas = Integer.MAX_VALUE;
            for (Integer count : rutinaContador.values()) {
                if (count < minHechas) {
                    minHechas = count;
                }
            }

            List<String> rutinasMenosHechas = new ArrayList<>();
            for (Map.Entry<String, Integer> entry : rutinaContador.entrySet()) {
                if (entry.getValue() == minHechas) {
                    rutinasMenosHechas.add(entry.getKey());
                }
            }

            return rutinasMenosHechas;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

    public void mostrarSugerencias(List<String> rutinas) {
        LinearLayout container = findViewById(R.id.usersContainer);
        container.removeAllViews();

        for (String rutina : rutinas) {
            View view = getLayoutInflater().inflate(R.layout.row_recommended_activities, null);
            TextView exerciseName = view.findViewById(R.id.exerciseName);
            TextView bodyPart = view.findViewById(R.id.bodyPart);

            exerciseName.setText(rutina);
            bodyPart.setText(""); // Si hay más información, agrégala aquí

            container.addView(view);
        }
    }
}
