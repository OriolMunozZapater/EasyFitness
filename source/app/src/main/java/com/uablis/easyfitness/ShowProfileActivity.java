package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class ShowProfileActivity extends AppCompatActivity {

    private static final String TAG = "ShowProfileActivity";

    private ImageView profileImageView, back_arrow;
    private TextView userNameTextView, userGenderTextView, userWeightTextView, userHeightTextView, userDescriptionTextView, userSocialLinksTextView;
    private Integer userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_profile);

        profileImageView = findViewById(R.id.profile_image);
        userNameTextView = findViewById(R.id.user_name);
        userGenderTextView = findViewById(R.id.user_gender);
        userWeightTextView = findViewById(R.id.user_weight);
        userHeightTextView = findViewById(R.id.user_height);
        userDescriptionTextView = findViewById(R.id.user_description);
        userSocialLinksTextView = findViewById(R.id.user_social_links);
        back_arrow = findViewById(R.id.back_arrow);
        //TODO: arreglar vista. Se ve con el usuario con el que tienes logueado.
        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get the user ID from the intent
        userId = getIntent().getIntExtra("userID", -1);

        // Log the retrieved userID
        Log.d(TAG, "Received userID: " + userId);

        if (userId == -1) {
            Log.e(TAG, "Invalid user ID");
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        Button btnTrainingRecords = findViewById(R.id.btnTrainingRecords);
        btnTrainingRecords.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia una nueva actividad o realiza una acción
                Intent intent = new Intent(ShowProfileActivity.this, TrainingRecordsActivity.class);
                intent.putExtra("userID", userId);
                startActivity(intent);
            }
        });

        // Fetch and display user profile
        fetchUserProfile();
    }

    private void fetchUserProfile() {
        ApiUrlBuilder apiUrlBuilder = new ApiUrlBuilder();
        String url = apiUrlBuilder.buildUrl("usuarios/" + userId + "/seguidos");

        Log.d(TAG, "Fetching user profile from URL: " + url);

        RequestQueue queue = Volley.newRequestQueue(this);

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            Log.d(TAG, "Response received: " + response.toString());

                            // Assuming there's only one user object in the array
                            if (response.length() > 0) {
                                JSONObject userObject = response.getJSONObject(0);

                                String userName = userObject.getString("nombre");
                                String userGender = userObject.getString("sexo");
                                String userWeight = userObject.getString("peso_actual");
                                String userHeight = userObject.getString("altura");
                                String userDescription = userObject.getString("descripcion");
                                String userSocialLinks = userObject.getString("redes_sociales");
                                String userProfileImage = userObject.isNull("foto") ? null : userObject.getString("foto");

                                userNameTextView.setText(userName);
                                userGenderTextView.setText(userGender);
                                userWeightTextView.setText(userWeight);
                                userHeightTextView.setText(userHeight);
                                userDescriptionTextView.setText(userDescription);
                                userSocialLinksTextView.setText(userSocialLinks);

                                // Load the user profile image (assume a method loadImage is available)
                                if (userProfileImage != null) {
                                    loadImage(userProfileImage, profileImageView);
                                }
                            } else {
                                Toast.makeText(ShowProfileActivity.this, "No user data found", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "JSON parsing error: " + e.getMessage(), e);
                            Toast.makeText(ShowProfileActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Volley error: " + error.getMessage(), error);
                if (error.networkResponse != null) {
                    Log.e(TAG, "Status code: " + error.networkResponse.statusCode);
                    Log.e(TAG, "Response data: " + new String(error.networkResponse.data));
                }
                Toast.makeText(ShowProfileActivity.this, "Error fetching data", Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(jsonArrayRequest);
    }

    private void loadImage(String url, ImageView imageView) {
        // Implement image loading logic here (e.g., using Glide or Picasso)
        Log.d(TAG, "Loading image from URL: " + url);
    }
}