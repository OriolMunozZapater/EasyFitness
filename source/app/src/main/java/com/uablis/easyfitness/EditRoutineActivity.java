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
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class EditRoutineActivity extends AppCompatActivity {
    private ImageView home, training_routines, training, profile;
    LinearLayout exerciseContainer;
    private EditText etEditRoutineName;
    private ImageView backArrow;
    private Button btnAddExercise, btnSave;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_routine);

        backArrow = findViewById(R.id.back_arrow);
        btnAddExercise = findViewById(R.id.btnAddExercise1);
        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        etEditRoutineName = findViewById(R.id.etEditRoutineName);
        btnSave = findViewById(R.id.btnSave);

        loadUserExercisesID();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        training_routines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToRoutines();
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

    private void updateUIWithExercises(String[] exerciseNames, Integer[] exerciseID, Integer rutinaID) {
        LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
        int pos=0;

        for (String name : exerciseNames) {

            View exerciseView = getLayoutInflater().inflate(R.layout.exercise_row, routinesLayout, false);
            TextView textView = exerciseView.findViewById(R.id.tvExerciseName);
            ImageButton eliminateCross = exerciseView.findViewById(R.id.eliminate_cross);

            textView.setText(name);

            exerciseView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goToEditExercise();
                }
            });

            int finalPos = pos;
            eliminateCross.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    deleteExercise(exerciseID[finalPos],rutinaID);
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
                //afegir rutina a la bd
                finish();
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

    private void loadUserExercisesID() {
        RequestQueue queue = Volley.newRequestQueue(this);
        int rutinaid = 1; //ESTABLECER ID BUENO
        String url = "http://192.168.0.19:8080/api/rutina_ejercicios/rutina/" + rutinaid;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            Integer[] exerciseID = new Integer[jsonArray.length()];

                            for (int i = 0; i < jsonArray.length(); i++) {
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                exerciseID[i] = jsonObject.getInt("ejercicioId");
                            }

                            loadUserExercisesName(exerciseID, rutinaid);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(EditRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }

    private void loadUserExercisesName(Integer[] exerciseID, Integer rutinaID) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String ids = TextUtils.join(",", exerciseID); // Convertir el array a una cadena separada por comas
        String url = "http://192.168.0.19:8080/api/ejercicios/name/" + ids;

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

                            updateUIWithExercises(exerciseNames, exerciseID, rutinaID); //FALTA CAMBIAR

                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(EditRoutineActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(EditRoutineActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });
        queue.add(stringRequest);
    }


    private boolean deleteExerciseFromDatabase(Integer exerciseID, Integer rutinaID) {
        String url="http://192.168.0.19:8080/api/rutina_ejercicios/delete/"+exerciseID+"/"+rutinaID;

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
                    }
                });

        // Agregar la solicitud a la cola de solicitudes
        queue.add(stringRequest);
        return true;
    }

    public void deleteExercise(Integer exerciseID, Integer rutinaID) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Estás seguro de eliminar?")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if(deleteExerciseFromDatabase(exerciseID, rutinaID)) {
                            Toast.makeText(EditRoutineActivity.this, "Ejercicio eliminado exitosamente", Toast.LENGTH_SHORT).show();
                            //RELOAD pantalla
                            LinearLayout routinesLayout = findViewById(R.id.exerciseContainer);
                            routinesLayout.removeAllViews();
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                                throw new RuntimeException(e);
                            }

                            loadUserExercisesID();
                        }else{
                            Toast.makeText(EditRoutineActivity.this, "Error al eliminar el ejercicio", Toast.LENGTH_SHORT).show();
                        }
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
        startActivity(intent);
    }
}