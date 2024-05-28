package com.uablis.easyfitness;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class AddComent extends AppCompatActivity {

    private ImageView imagePublic;
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private ImageButton dislikeButton, likeButton;
    private boolean isLiked = false;
    private boolean isDisliked = false;
    private EditText comment;
    private ImageView backArrow;
    private Button btnPublish;
    private Integer ejercicioID;
    private ImageView home, trainingRoutinesButton, profile, training_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_coment);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddComent.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AddComent.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(AddComent.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(AddComent.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        bindViews();
        ejercicioID=getIntent().getIntExtra("EJERCICIO_ID", 0); // 0 es un valor predeterminado en caso de no encontrarse el dato.
        btnPublish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                backToScreen();
            }
        });

        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isLiked) {
                    likeButton.setImageResource(R.drawable.baseline_thumb_up_off_alt_24); // Icono no seleccionado
                    isLiked = false;
                } else {
                    likeButton.setImageResource(R.drawable.baseline_thumb_up_alt_24); // Icono seleccionado
                    isLiked = true;
                    if (isDisliked) {
                        dislikeButton.setImageResource(R.drawable.baseline_thumb_down_off_alt_24); // Desactivar dislike
                        isDisliked = false;
                    }
                }
            }
        });

        dislikeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isDisliked) {
                    dislikeButton.setImageResource(R.drawable.baseline_thumb_down_off_alt_24); // Icono no seleccionado
                    isDisliked = false;
                } else {
                    dislikeButton.setImageResource(R.drawable.baseline_thumb_down_alt_24); // Icono seleccionado
                    isDisliked = true;
                    if (isLiked) {
                        likeButton.setImageResource(R.drawable.baseline_thumb_up_off_alt_24); // Desactivar like
                        isLiked = false;
                    }
                }
            }
        });


    }

    private void bindViews() {

        imagePublic = findViewById(R.id.imagePublic);
        dislikeButton = findViewById(R.id.dislike);
        likeButton = findViewById(R.id.like);
        comment = findViewById(R.id.comment);
        backArrow = findViewById(R.id.back_arrow);
        btnPublish = findViewById(R.id.btnPublish);
    }

    private void setExerciseComent() {
        JSONObject jsonBody = new JSONObject();
        int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
        String comentario = comment.getText().toString().trim();
        boolean like=false;

        if(isLiked)
            like=true;

        if(comentario.isEmpty() || (!isLiked && !isDisliked)) {
            Toast.makeText(AddComent.this, "Es necesario rellenar todos los campos", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            jsonBody.put("userID", userID);
            jsonBody.put("ejercicioID", ejercicioID);
            jsonBody.put("valoracion", like);
            jsonBody.put("comentario", comentario);
        } catch (JSONException e) {
            e.printStackTrace();
            return;
        }
        sendUpdateRequest(jsonBody);
    }

    private void sendUpdateRequest(JSONObject requestBody) {
        String path = "valoracion_ejercicio/crear";
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AddComent.this, "Comentario creado correctamente", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AddComent.this, "Error al añadir el comentario: " + error.toString(), Toast.LENGTH_SHORT).show();
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

    public void save() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to publish this?");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //afegir rutina a la bd
                setExerciseComent();
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

    public void backToScreen() {
        finish();
    }
}