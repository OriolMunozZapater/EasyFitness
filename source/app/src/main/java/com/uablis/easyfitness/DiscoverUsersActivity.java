package com.uablis.easyfitness;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DiscoverUsersActivity extends AppCompatActivity {

    private LinearLayout usersContainer;
    private ImageView back_arrow;
    private Integer userId = Integer.parseInt(UsuarioActual.getInstance().getUserId());
    private ImageView home, trainingRoutinesButton, profile, training_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_users);  // Ensure this layout has a LinearLayout (usersContainer)
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverUsersActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DiscoverUsersActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(DiscoverUsersActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(DiscoverUsersActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        back_arrow = findViewById(R.id.back_arrow);
        usersContainer = findViewById(R.id.usersContainer);

        back_arrow.setOnClickListener(v -> finish());

        fetchAllUsers();
    }

    private void fetchAllUsers() {
        ApiUrlBuilder apiUrlBuilder = new ApiUrlBuilder();
        String url = apiUrlBuilder.buildUrl("usuarios/all_users");

        RequestQueue queue = Volley.newRequestQueue(this);
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                response -> {
                    try {
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject userObject = response.getJSONObject(i);
                            int retrievedUserId = userObject.getInt("userID");
                            if (retrievedUserId != this.userId && retrievedUserId != 0) {  // Compara el userID con el del usuario actual
                                addUserToContainer(userObject);
                            }
                        }
                    } catch (JSONException e) {
                        Toast.makeText(DiscoverUsersActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                    }
                }, error -> Toast.makeText(DiscoverUsersActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show());

        queue.add(jsonArrayRequest);
    }


    private void addUserToContainer(JSONObject userObject) throws JSONException {
        String userName = userObject.getString("nombre") + " " + userObject.getString("apellido");
        int userId = userObject.getInt("userID");

        LayoutInflater inflater = LayoutInflater.from(this);
        View rowView = inflater.inflate(R.layout.row_user_followers, usersContainer, false);  // Make sure this layout file exists and is suitable

        TextView userNameTextView = rowView.findViewById(R.id.userName);
        userNameTextView.setText(userName);

        Button followButton = rowView.findViewById(R.id.follow_button);
        checkFollowStatus(userId, followButton);

        usersContainer.addView(rowView);
    }

    private void checkFollowStatus(int followerId, Button followButton) {
        ApiUrlBuilder apiUrlBuilder = new ApiUrlBuilder();
        String url = apiUrlBuilder.buildUrl("usuarios/" + userId + "/seguidos");

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        boolean isFollowing = false;
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject followedUser = response.getJSONObject(i);
                                if (followedUser.getInt("userID") == followerId) {
                                    isFollowing = true;
                                    break;
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        if (isFollowing) {
                            setupUnfollowButton(followButton, followerId);
                        } else {
                            setupFollowButton(followButton, followerId);
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(DiscoverUsersActivity.this, "Error checking follow status", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void setupFollowButton(Button followButton, int followerId) {
        followButton.setText("Follow");
        followButton.setBackgroundColor(Color.LTGRAY);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                followUser(followerId, followButton);
            }
        });
    }

    private void setupUnfollowButton(Button followButton, int followerId) {
        followButton.setText("Unfollow");
        followButton.setBackgroundColor(Color.DKGRAY);
        followButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                unfollowUser(followerId, followButton);
            }
        });
    }

    private void followUser(int followedId, Button followButton) {
        ApiUrlBuilder apiUrlBuilder = new ApiUrlBuilder();
        String url = apiUrlBuilder.buildUrl("usuarios/" + userId + "/seguir/" + followedId);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DiscoverUsersActivity.this, "Now following", Toast.LENGTH_SHORT).show();
                        // Update button state to unfollow
                        setupUnfollowButton(followButton, followedId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error details
                String errorMessage = "Error following user: " + error.toString();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMessage += ", Response: " + new String(error.networkResponse.data);
                }
                Toast.makeText(DiscoverUsersActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }

    private void unfollowUser(int followedId, Button followButton) {
        ApiUrlBuilder apiUrlBuilder = new ApiUrlBuilder();
        String url = apiUrlBuilder.buildUrl("usuarios/" + userId + "/dejarSeguir/" + followedId);

        RequestQueue queue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.DELETE, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Toast.makeText(DiscoverUsersActivity.this, "Unfollowed", Toast.LENGTH_SHORT).show();
                        // Update button state to follow
                        setupFollowButton(followButton, followedId);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                // Log the error details
                String errorMessage = "Error unfollowing user: " + error.toString();
                if (error.networkResponse != null && error.networkResponse.data != null) {
                    errorMessage += ", Response: " + new String(error.networkResponse.data);
                }
                Toast.makeText(DiscoverUsersActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                error.printStackTrace();
            }
        });

        queue.add(stringRequest);
    }
}