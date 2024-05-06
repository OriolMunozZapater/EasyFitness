package com.uablis.easyfitness;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Button;
import android.view.View;
import android.content.Intent;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

public class NewRoutineActivity extends AppCompatActivity {

    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private ImageButton eliminateCross;
    private ImageView home, training_routines, training, profile;
    LinearLayout exerciseContainer;
    private EditText etEditRoutineName;
    private ImageView backArrow;
    private Button btnAddExercise, btnSaveRoutine;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_routine);

        backArrow = findViewById(R.id.back_arrow);
        btnAddExercise = findViewById(R.id.btnAddExercise1);
        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        btnSaveRoutine = findViewById(R.id.btnSaveNewRoutine);

        training_routines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRoutines();
            }
        });

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

    public void saveRoutine() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to create a new routine?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //String jsonEjerciciosID = sharedPreferences.getString("ejerciciosID", "");
                //int[] ejerciciosID = new Gson().fromJson(jsonEjerciciosID, int[].class);
                //addRoutine(ejerciciosID);
                //finish();
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
        finish();
    }

    public void goToEditExercise(){
        Intent intent = new Intent(NewRoutineActivity.this, EditExerciseActivity.class);
        startActivity(intent);
    }
    public void backToScreen() {
        finish();
    }

    public void deleteExercise() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de eliminar?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
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
        startActivity(intent);
    }

    public void addRoutine(int[] ejerciciosID){
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("nombre", etEditRoutineName.getText().toString().trim());
            jsonBody.put("user_ID", UsuarioActual.getInstance().getUserId());

            sendUpdateRequest(jsonBody);

            //esperar a que se haga el insert en la BBDD
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            getRutinaID(ejerciciosID);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void validateFields() {
        etEditRoutineName.getText();
    }

    private void sendUpdateRequest(JSONObject jsonBody) {
        String url = "http://192.168.100.1:8080/api/rutinas";

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, response -> {
            validateFields();
        }, Throwable::printStackTrace) {
            @Override
            public byte[] getBody() {
                return jsonBody.toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };
        queue.add(stringRequest);
    }

    public void getRutinaID(int[] ejerciciosID){
        RequestQueue queue = Volley.newRequestQueue(this);
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        //  String url = "http://192.168.100.1:8080/api/rutinas/last/" + userID;
        String path = "rutinas/last/" + userID;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            int rutinaID;

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                rutinaID = Integer.parseInt(jsonObject.getString("rutinaID"));
                                for(int ejercicioID : ejerciciosID) {
                                    addEjercicioRutina(ejercicioID, rutinaID);
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(NewRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(NewRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }
    public void addEjercicioRutina(int ejercicioID, int rutinaID){
        return;
    }
}