package com.uablis.easyfitness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
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

public class CommentsActivity extends AppCompatActivity {

    private LinearLayout commentsContainer;
    private EditText editTextComment;
    private Button buttonSendComment;
    private String routineId;
    private RequestQueue requestQueue;
    private ApiUrlBuilder urlBase = new ApiUrlBuilder();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments_routine);

        commentsContainer = findViewById(R.id.commentsContainer);
        editTextComment = findViewById(R.id.editTextComment);
        buttonSendComment = findViewById(R.id.buttonSendComment);
        requestQueue = Volley.newRequestQueue(this);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        routineId = getIntent().getStringExtra("ROUTINE_ID");

        ImageView backArrow = findViewById(R.id.back_arrow);
        backArrow.setOnClickListener(v -> finish());

        buttonSendComment.setOnClickListener(v -> sendComment());

        loadComments();
    }

    private void loadComments() {
        String url = urlBase.buildUrl("rutina_comentarios/rutina/" + routineId);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject comment = response.getJSONObject(i);
                            String userId = comment.getString("idUsuario");
                            String commentText = comment.getString("comentario");
                            String commentId = comment.getString("idComentario"); // Suponiendo que este es el campo ID para el comentario
                            fetchUserNameAndAddComment(userId, commentText, commentId);
                        }
                    } catch (JSONException e) {
                        Toast.makeText(CommentsActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                },
                error -> {
                    //Nothing bc always pushes error even if correct
                });
        requestQueue.add(jsonArrayRequest);
    }

    private void fetchUserNameAndAddComment(String userId, String commentText, String commentId) {
        String userUrl = urlBase.buildUrl("usuarios/" + userId);
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, userUrl, null,
                response -> {
                    try {
                        String userName = response.getString("nombre");
                        addCommentToLayout(userName, commentText, commentId);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    Toast.makeText(this, "Error fetching user name", Toast.LENGTH_SHORT).show();
                    error.printStackTrace();
                });
        requestQueue.add(jsonObjectRequest);
    }

    private void sendComment() {
        String commentText = editTextComment.getText().toString().trim();
        if (commentText.isEmpty()) {
            Toast.makeText(this, "El comentario no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        String url = urlBase.buildUrl("rutina_comentarios");
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("idRutina", routineId);
            jsonBody.put("idUsuario", UsuarioActual.getInstance().getUserId()); // Suponiendo que tienes una clase UsuarioActual para obtener el usuario actual
            jsonBody.put("comentario", commentText);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, jsonBody,
                response -> {
                    try {
                        // Obtener el ID del usuario desde la respuesta o directamente desde UsuarioActual
                        String userId = response.getString("idUsuario");
                        // Obtener el ID del comentario desde la respuesta
                        String commentId = response.getString("idComentario");
                        // Utilizar fetchUserNameAndAddComment para obtener el nombre del usuario y añadir el comentario
                        fetchUserNameAndAddComment(userId, commentText, commentId);
                        editTextComment.setText("");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                },
                error -> {
                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Toast.makeText(this, "Error al enviar comentario: " + responseBody, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al enviar comentario: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json");
                // Agrega otros headers si es necesario
                return headers;
            }
        };

        requestQueue.add(jsonObjectRequest);
    }


    private void addCommentToLayout(String userName, String commentText, String commentId) {
        View commentView = getLayoutInflater().inflate(R.layout.comment_routine_item, commentsContainer, false);
        TextView textViewUserName = commentView.findViewById(R.id.textViewUserName);
        TextView textViewComment = commentView.findViewById(R.id.textViewComment);
        ImageButton buttonDeleteComment = commentView.findViewById(R.id.buttonDeleteComment); // Usar ImageButton

        textViewUserName.setText(userName);
        textViewComment.setText(commentText);

        buttonDeleteComment.setOnClickListener(v -> deleteComment(commentId, commentView));

        commentsContainer.addView(commentView);
    }



    private void deleteComment(String commentId, View commentView) {
        String url = urlBase.buildUrl("rutina_comentarios/" + commentId);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                response -> {
                    commentsContainer.removeView(commentView);
                    Toast.makeText(this, "Comentario eliminado", Toast.LENGTH_SHORT).show();
                },
                error -> {
                    if (error.networkResponse != null) {
                        String responseBody = new String(error.networkResponse.data, StandardCharsets.UTF_8);
                        Toast.makeText(this, "Error al eliminar comentario: " + responseBody, Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(this, "Error al eliminar comentario: " + error.toString(), Toast.LENGTH_LONG).show();
                    }
                });

        requestQueue.add(stringRequest);
    }

}
