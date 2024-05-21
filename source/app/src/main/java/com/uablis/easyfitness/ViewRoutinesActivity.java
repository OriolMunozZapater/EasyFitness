package com.uablis.easyfitness;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

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

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ViewRoutinesActivity extends AppCompatActivity {

    private LinearLayout routinesContainer;
    private RequestQueue requestQueue;
    private ApiUrlBuilder urlBase = new ApiUrlBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_routines);

        routinesContainer = findViewById(R.id.routinesContainer);
        requestQueue = Volley.newRequestQueue(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> finish());

        loadRoutines();
    }

    private void loadRoutines() {
        String url = urlBase.buildUrl("rutinas/publicas");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject routine = response.getJSONObject(i);
                            String routineName = routine.getString("nombre");
                            String routineId = routine.getString("rutinaID");
                            int userId = routine.getInt("userID");
                            loadUserName(routineName, routineId, userId);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(ViewRoutinesActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Toast.makeText(this, "Network error: " + responseBody, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Network error: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void loadUserName(String routineName, String routineId, int userId) {
        String url = urlBase.buildUrl("usuarios/" + userId);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    try {
                        JSONObject user = new JSONObject(response);
                        String userName = user.optString("nombre", "Nombre desconocido");
                        addRoutineToLayout(routineName, userName, routineId);
                    } catch (JSONException e) {
                        Toast.makeText(ViewRoutinesActivity.this, "JSON format error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> Toast.makeText(ViewRoutinesActivity.this, "Error loading user name: " + error.getMessage(), Toast.LENGTH_SHORT).show());
        requestQueue.add(stringRequest);
    }

    private void addRoutineToLayout(String routineName, String userName, String routineId) {
        View routineView = getLayoutInflater().inflate(R.layout.routine_item3, routinesContainer, false);
        TextView textViewRoutineName = routineView.findViewById(R.id.textViewRoutineName);
        TextView textViewUserName = routineView.findViewById(R.id.textViewUserName);
        TextView textViewRoutineId = routineView.findViewById(R.id.textViewRoutineId);
        textViewRoutineId.setText(routineId);

        textViewRoutineName.setText(routineName);
        textViewUserName.setText("Autor: " + userName);

        routinesContainer.addView(routineView);
    }

    public void onCopyRoutineClicked(View view) {
        ViewGroup parent = (ViewGroup) view.getParent();
        TextView textViewRoutineId = parent.findViewById(R.id.textViewRoutineId);
        String routineID = textViewRoutineId.getText().toString();
        copyRoutineToUser(routineID, UsuarioActual.getInstance().getUserId());
    }

    private void copyRoutineToUser(String routineID, String userID) {
        showAddingRoutinePopup();
        RequestQueue queue = Volley.newRequestQueue(this);
        String path = "rutinas/copiar/" + routineID;
        String url = urlBase.buildUrl(path);

        JSONObject jsonBody = new JSONObject();
        try {
            // Parsear el userID a entero si es necesario
            int userIdInt = Integer.parseInt(userID);
            jsonBody.put("userID", userIdInt);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                },
                error -> {
                    error.printStackTrace();
                    String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                    Toast.makeText(this, "Error al añadir rutina: " + responseBody, Toast.LENGTH_LONG).show();
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // Si hay headers de autenticación u otros, agrégalos aquí
                return headers;
            }
        };

        queue.add(jsonObjectRequest);
    }

    private void showAddingRoutinePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);

        AlertDialog dialog = builder.create();
        dialog.show();

        new Handler().postDelayed(() -> {
            dialog.dismiss();
            Intent intent = new Intent(ViewRoutinesActivity.this, MainNetworkActivity.class);
            startActivity(intent);
        }, 3000);
    }
}
