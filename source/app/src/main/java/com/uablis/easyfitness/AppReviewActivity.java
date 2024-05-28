package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

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

public class AppReviewActivity extends AppCompatActivity {
    private EditText description;
    private ImageButton removeInfo;
    private Button btnEnviarReview;
    private ImageView backArrow;
    private Toolbar toolbar;
    ApiUrlBuilder urlBase = new ApiUrlBuilder();
    private ImageView home, trainingRoutinesButton, profile, training_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);
        description = findViewById(R.id.description);
        removeInfo = findViewById(R.id.removeInfo);
        btnEnviarReview = findViewById(R.id.btnEnviarReview);
        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.back_arrow);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppReviewActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AppReviewActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(AppReviewActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(AppReviewActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        btnEnviarReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enviarReview();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish(); // Terminar la actividad y volver a la anterior
            }
        });

        removeInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.warning_init).setVisibility(View.GONE);
            }
        });

        // Configurar la barra de herramientas
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
    }

    private void enviarReview() {

        String reviewDescription = description.getText().toString();

        if(!reviewDescription.isEmpty())
        {
            JSONObject jsonBody = new JSONObject();
            int userID = Integer.parseInt(UsuarioActual.getInstance().getUserId());
            try {
                jsonBody.put("userID", userID);
                jsonBody.put("descripcion", reviewDescription);
            } catch (JSONException e) {
                e.printStackTrace();
                return;
            }
            sendUpdateRequest(jsonBody);
        }else
            Toast.makeText(this, "ERROR: campo vacío, por favor rellene la descripcion", Toast.LENGTH_SHORT).show();

    }

    private void sendUpdateRequest(JSONObject requestBody) {
        String path = "comentarios/crear";
        String url = urlBase.buildUrl(path);

        RequestQueue queue = Volley.newRequestQueue(this);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(AppReviewActivity.this, "Comentario enviado correctamente. Gracias!", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(AppReviewActivity.this, "Error al crear el comentario: " + error.toString(), Toast.LENGTH_SHORT).show();
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