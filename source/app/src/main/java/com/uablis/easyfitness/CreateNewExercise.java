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
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import android.widget.VideoView;

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
    private EditText etExerciseName, etExerciseDescrp;
    private Button btnMuscleSelect, btnCreate;
    private CharSequence[] muscle_options = {"Chest", "Back", "Biceps", "Triceps", "Shoulders", "Quads", "Abs", "Isquios"};
    private ImageView home, trainingRoutinesButton, profile, training_session;
    private Integer exerciseID;
    private static final int PICK_VIDEO_REQUEST = 1, PICK_IMAGE_REQUEST = 2;
    private ImageView backArrow, imageExercise;
    private VideoView videoExercise;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_exercise);

        btnMuscleSelect = findViewById(R.id.btnMuscleSelect);
        etExerciseDescrp = findViewById(R.id.exDesc);
        etExerciseName = findViewById(R.id.exName);
        videoExercise = findViewById(R.id.videoExercise);
        imageExercise = findViewById(R.id.imageExercise);
        btnCreate = findViewById(R.id.btnSaveExercise);
        backArrow = findViewById(R.id.backArrow);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewExercise.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CreateNewExercise.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(CreateNewExercise.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(CreateNewExercise.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

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
            }
        });
    }

    public void createExercise() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to create this exercise?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                setNewExercise();
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
                Intent returnIntent = new Intent();
                setResult(RESULT_OK, returnIntent);
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

    public void selectVideoFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        intent.setType("video/*");
        startActivityForResult(Intent.createChooser(intent, "Select Video"), PICK_VIDEO_REQUEST);
    }

    public void selectImageFromGallery(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_VIDEO_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            videoExercise.setVideoURI(uri);
            videoExercise.start();
            btnCreate.setOnClickListener(v -> uploadMediaVideo(uri));
        } else  if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            imageExercise.setImageURI(uri); // Set image temporarily
            btnCreate.setOnClickListener(v -> uploadMedia(uri));
        }
    }

    private void getNewExerciseID(Uri fileUri) {
        RequestQueue queue = Volley.newRequestQueue(this);
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        String path = "ejercicios/last/" + userID;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        exerciseID = Integer.parseInt(response);
                        uploadImageToFirebaseStorage(fileUri);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CreateNewExercise.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                if (error.networkResponse != null) {
                    Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                    Log.e("VolleyError", "Response Data: " + new String(error.networkResponse.data));
                }
            }
        });
        queue.add(stringRequest);
    }

    private void getNewExerciseIDVideo(Uri fileUri) {
        RequestQueue queue = Volley.newRequestQueue(this);
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        String path = "ejercicios/last/" + userID;
        String url = urlBase.buildUrl(path);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        exerciseID = Integer.parseInt(response);
                        uploadVideoToFirebaseStorage(fileUri);

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(CreateNewExercise.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
                if (error.networkResponse != null) {
                    Log.e("VolleyError", "Status Code: " + error.networkResponse.statusCode);
                    Log.e("VolleyError", "Response Data: " + new String(error.networkResponse.data));
                }
            }
        });
        queue.add(stringRequest);
    }

    private void uploadVideoToFirebaseStorage(Uri fileUri) {
        if (fileUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("videos/exerciseID" + exerciseID);

            storageReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String videoUrl = downloadUri.toString();
                        videoExercise.setVideoURI(Uri.parse(videoUrl)); }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateNewExercise.this, "Error uploading video: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error uploading video", e);
                    });
        }
    }

    private void uploadMedia(Uri fileUri) {
        setNewExercise();
        getNewExerciseID(fileUri);
    }

    private void uploadMediaVideo(Uri fileUri) {
        setNewExercise();
        getNewExerciseIDVideo(fileUri);
    }


    private void uploadImageToFirebaseStorage(Uri fileUri) {
        if (fileUri != null) {
            StorageReference storageReference = FirebaseStorage.getInstance().getReference("images/exerciseID" + exerciseID);

            storageReference.putFile(fileUri)
                    .addOnSuccessListener(taskSnapshot -> storageReference.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                        String imageUrl = downloadUri.toString();
                        imageExercise.setImageURI(Uri.parse(imageUrl)); }))
                    .addOnFailureListener(e -> {
                        Toast.makeText(CreateNewExercise.this, "Error uploading image: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.e("Firebase", "Error uploading image", e);
                    });
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

    private void setNewExercise() {
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
            jsonBody.put("tipo","a"); //TODO ELIMINAR
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
                        Intent returnIntent = new Intent();
                        setResult(RESULT_OK, returnIntent);
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