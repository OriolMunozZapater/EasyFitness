package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

public class CreateNewExercise extends AppCompatActivity {
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private static final int PICK_IMAGE_REQUEST = 1;
    private ImageView imageExercise, backArrow;
    private EditText etExerciseName, etExerciseDescrp;
    private Button btnMuscleSelect, btnCreate;
    private CharSequence[] muscle_options = {"Chest", "Back", "Biceps", "Triceps", "Shoulders", "Quads", "Abs", "Isquios"};


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_exercise);

        btnMuscleSelect = findViewById(R.id.btnMuscleSelect);
        etExerciseDescrp = findViewById(R.id.exDesc);
        etExerciseName = findViewById(R.id.exName);
        imageExercise = findViewById(R.id.imageExercise);
        btnCreate = findViewById(R.id.btnSaveExercise);
        backArrow = findViewById(R.id.backArrow);

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backScreen();
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createExercise();
                //finish();
            }
        });
    }

    public void createExercise() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to create this exercise?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //Intent intent = new Intent(CreateNewExercise.this, ChooseExerciseActivity.class);
                //startActivity(intent);

                //afegir exercici a la bd

                setNewExerciseNoVideo();

                //Toast.makeText(CreateNewExercise.this, "Ejercicio creado correctamente", Toast.LENGTH_SHORT).show();

            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // No hacer nada, simplemente cerrar el diálogo
                dialog.dismiss();
                Toast.makeText(CreateNewExercise.this, "Error al crear el ejercicio", Toast.LENGTH_SHORT).show();
            }
        });
        builder.show();
    }

    public void backScreen() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you don't want to create a new exercise?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
    public void selectImageFromGallery(View view) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Aquí puedes setear el bitmap en tu ImageView
                imageExercise.setImageBitmap(bitmap);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void showMuscleOptions(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Selecciona el grupo muscular");
        builder.setItems(muscle_options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String selectedMuscle = muscle_options[which].toString();
                btnMuscleSelect.setText(selectedMuscle);
            }
        });
        builder.show();
    }

    private boolean validateFields() {
        return (etExerciseName.getText() != null &&
                etExerciseDescrp.getText() != null && btnMuscleSelect.getText() != null ||
                imageExercise.getDrawable() != null);
    }

    private void setNewExercise() {
        JSONObject jsonBody = new JSONObject();
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        try {
            jsonBody.put("userID", userID);
            jsonBody.put("nombre", etExerciseName.getText().toString().trim());
            jsonBody.put("descripcion", etExerciseDescrp.getText().toString().trim());
            jsonBody.put("grupo_muscular", btnMuscleSelect.getText().toString().trim());
            //jsonBody.put("video", imageExercise.getDrawable().toString());
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        sendUpdateRequest(jsonBody);
    }

    private void setNewExerciseNoVideo() {
        JSONObject jsonBody = new JSONObject();
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        String nombre = etExerciseName.getText().toString().trim();
        String descripcion = etExerciseDescrp.getText().toString().trim();
        String grupoMuscular = btnMuscleSelect.getText().toString().trim();

        if(nombre.isEmpty() || descripcion.isEmpty() || grupoMuscular.equals("Select Muscle")) {
            Toast.makeText(CreateNewExercise.this, "Es necesario rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            jsonBody.put("userID", userID);
            jsonBody.put("nombre", nombre);
            jsonBody.put("descripcion", descripcion);
            jsonBody.put("grupoMuscular", grupoMuscular);
            //IF VIDEO/FOTO

        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        sendUpdateRequest(jsonBody);
    }

    private void sendUpdateRequest(JSONObject requestBody) {
        //String url = "http://192.168.100.1:8080/api/ejercicio/";
        String path = "ejercicios/crear";
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(CreateNewExercise.this, "Ejercicio creado correctamente", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Ocurrió un error al hacer la solicitud
                        error.printStackTrace();
                        Log.e("VolleyError", "Error: " + error.toString());
                        if (error.networkResponse != null) {
                            Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                            Log.e("VolleyError", "Response Data: " + new String(error.networkResponse.data));
                        }
                        Toast.makeText(CreateNewExercise.this, "Error al crear el ejercicio: " + error.toString(), Toast.LENGTH_SHORT).show();
                    }

                }
        ) {
            @Override
            public byte[] getBody() {
                return requestBody.toString().getBytes(StandardCharsets.UTF_8);
            }
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        queue.add(stringRequest);
    }


}