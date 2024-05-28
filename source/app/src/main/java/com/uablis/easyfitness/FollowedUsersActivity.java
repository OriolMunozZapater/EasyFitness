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
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.Instant;

public class FollowedUsersActivity extends AppCompatActivity {

    private static final String TAG = "FollowedUsersActivity";

    private ImageView home, trainingRoutinesButton, profile, training_session, back_arrow;
    private LinearLayout usersContainer;
    private Integer userId = Integer.parseInt(UsuarioActual.getInstance().getUserId());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_followed_users);

        back_arrow = findViewById(R.id.back_arrow);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);
        usersContainer = findViewById(R.id.usersContainer);

        // Configurar los listeners de clic para los botones
        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowedUsersActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FollowedUsersActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(FollowedUsersActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(FollowedUsersActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
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
                                String userProfileImage = userObject.getString("foto");
                                int followedId = userObject.getInt("userID");

                                LayoutInflater inflater = LayoutInflater.from(FollowedUsersActivity.this);
                                View rowView = inflater.inflate(R.layout.row_user_followed, usersContainer, false);

                                TextView userNameTextView = rowView.findViewById(R.id.userName);
                                userNameTextView.setText(userName);

                                Button viewProfileButton = rowView.findViewById(R.id.view_profile_button);
                                Button viewVideosButton = rowView.findViewById(R.id.view_videos_button);

                                viewProfileButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FollowedUsersActivity.this, ShowProfileActivity.class);
                                        intent.putExtra("userID", followedId);
                                        startActivity(intent);
                                    }
                                });

                                viewVideosButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(FollowedUsersActivity.this, ViewVideosActivity.class);
                                        intent.putExtra("userID", followedId);
                                        startActivity(intent);
                                    }
                                });

                                ImageView profileImageView = rowView.findViewById(R.id.profile_image);
                                String stringID = followedId + "";

                                loadImage(stringID, profileImageView);
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

    private void loadImage(String userId, ImageView imageView) {
        StorageReference storageRef = FirebaseStorage.getInstance().getReference("images/" + "userID" + userId);

        storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
            Glide.with(this)
                    .load(uri)
                    .placeholder(R.drawable.default_image)
                    .into(imageView);
        }).addOnFailureListener(e -> {
            imageView.setImageResource(R.drawable.default_image);
        });
    }

}