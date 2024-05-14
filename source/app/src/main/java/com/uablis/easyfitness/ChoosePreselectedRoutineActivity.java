package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class ChoosePreselectedRoutineActivity extends AppCompatActivity {
    private ImageView home, training_routines, training, profile, backArrow;
    ApiUrlBuilder urlBase = new ApiUrlBuilder();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_preselected_routine);

        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        backArrow = findViewById(R.id.back_arrow);


        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backScreen();
            }
        });
        loadPredifinedRoutine();
    }

    private void loadPredifinedRoutine() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String path = "rutinas/usuario/" + 0;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            String[] routineIDs = new String[jsonArray.length()];
                            String[] routineNames = new String[jsonArray.length()];
                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                routineIDs[i] = jsonObject.getString("rutinaID");
                                routineNames[i] = jsonObject.getString("nombre");
                            }
                            updateUIWithRoutines(routineNames, routineIDs, userId);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ChoosePreselectedRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ChoosePreselectedRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }
    private void updateUIWithRoutines(String[] routineNames, String[] routineIDs, String userID) {
        LinearLayout routinesLayout = findViewById(R.id.preRoutineContainer);
        routinesLayout.removeAllViews();

        for (int i = 0; i < routineNames.length; i++) {
            final String routineIdFinal = routineIDs[i];

            View routineView = getLayoutInflater().inflate(R.layout.routine_item, routinesLayout, false);
            TextView textView = routineView.findViewById(R.id.textViewRoutineName);
            ImageButton addButton = routineView.findViewById(R.id.menu_button_routine);

            textView.setText(routineNames[i]);
            addButton.setImageResource(R.drawable.ic_add);
            addButton.setOnClickListener(v -> addRoutineToProfile(routineIdFinal));

            routinesLayout.addView(routineView);
        }
    }

    private void addRoutineToProfile(String routineID) {
        // Aquí necesitamos implementar la lógica para copiar la rutina al perfil del usuario
        Toast.makeText(this, "Adding Routine ID: " + routineID, Toast.LENGTH_SHORT).show();
        copyRoutineToUser(routineID, UsuarioActual.getInstance().getUserId());
    }

    private void gotomyRoutines() {
        Intent intent = new Intent(ChoosePreselectedRoutineActivity.this, TrainingRoutinesActivity.class);
        startActivity(intent);
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

    public void backScreen() {
        finish();
    }

    private void showAddingRoutinePopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(false);
        builder.setView(R.layout.loading_dialog);

        AlertDialog dialog = builder.create();
        dialog.show();

        // Cerrar el diálogo después de 3 segundos (3000 milisegundos)
        new Handler().postDelayed(() -> {
            dialog.dismiss();
            gotomyRoutines();
        }, 3000);
    }
}