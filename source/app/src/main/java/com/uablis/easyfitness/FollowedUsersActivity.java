package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FollowedUsersActivity extends AppCompatActivity {

    private static final String TAG = "FollowedUsersActivity";

    private ImageView homeButton, trainingRoutinesButton, trainingSessionButton, profileButton, back_arrow;
    private LinearLayout usersContainer;
    private Integer userId = Integer.parseInt(UsuarioActual.getInstance().getUserId());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_users);

        back_arrow = findViewById(R.id.back_arrow);
        homeButton = findViewById(R.id.home);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        trainingSessionButton = findViewById(R.id.training_session);
        profileButton = findViewById(R.id.profile);
        usersContainer = findViewById(R.id.usersContainer);

        // Configurar los listeners de clic para los botones
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de inicio
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
            }
        });

        trainingSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de sesión de entrenamiento
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de perfil
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Fetch followed users
        fetchFollowedUsers();
    }

    private void fetchFollowedUsers() {
        ApiUrlBuilder apiUrlBuilder = new ApiUrlBuilder();
        String url = apiUrlBuilder.buildUrl("usuarios/" + userId + "/seguidos");

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                JSONObject userObject = response.getJSONObject(i);
                                String userName = userObject.getString("nombre") + " " + userObject.getString("apellido");
                                String userProfileImage = userObject.getString("foto"); // Assuming the photo URL is provided
                                int followedId = userObject.getInt("userID");

                                // Log the followedId
                                Log.d(TAG, "Followed userID: " + followedId);

                                // Inflar el layout row_user_followed.xml
                                LayoutInflater inflater = LayoutInflater.from(FollowedUsersActivity.this);
                                View rowView = inflater.inflate(R.layout.row_user_followed, usersContainer, false);

                                // Rellenar los datos del usuario
                                TextView userNameTextView = rowView.findViewById(R.id.userName);
                                userNameTextView.setText(userName);

                                ImageView profileImageView = rowView.findViewById(R.id.profile_image);
                                // Load the user profile image (assume a method loadImage is available)
                                loadImage(userProfileImage, profileImageView);

                                Button viewProfileButton = rowView.findViewById(R.id.view_profile_button);
                                viewProfileButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FollowedUsersActivity.this, ShowProfileActivity.class);
                                        intent.putExtra("userID", followedId);
                                        Log.d(TAG, "Passing userID: " + followedId + " to ShowProfileActivity");
                                        startActivity(intent);
                                    }
                                });

                                // Añadir la vista inflada al contenedor
                                usersContainer.addView(rowView);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(FollowedUsersActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(FollowedUsersActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void loadImage(String url, ImageView imageView) {
        // Implement image loading logic here (e.g., using Glide or Picasso)
        // Example with Glide:
        // Glide.with(this).load(url).into(imageView);
    }
}