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

import java.util.HashSet;
import java.util.Set;

public class DiscoverUsersActivity extends AppCompatActivity {

    private static final String TAG = "DiscoverUsersActivity";

    private ImageView homeButton, trainingRoutinesButton, trainingSessionButton, profileButton, back_arrow;
    private LinearLayout usersContainer;
    private Integer userId;
    private Set<Integer> followedUserIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_discover_users);

        back_arrow = findViewById(R.id.back_arrow);
        homeButton = findViewById(R.id.home);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        trainingSessionButton = findViewById(R.id.training_session);
        profileButton = findViewById(R.id.profile);
        usersContainer = findViewById(R.id.usersContainer);

        // Set up click listeners for buttons
        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for home button
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for training routines button
            }
        });

        trainingSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for training session button
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Logic for profile button
            }
        });

        back_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Get userId from UsuarioActual instance
        try {
            String userIdStr = UsuarioActual.getInstance().getUserId();
            if (userIdStr == null || userIdStr.isEmpty()) {
                throw new NumberFormatException("userId is null or empty");
            }
            userId = Integer.parseInt(userIdStr);
            Log.d(TAG, "User ID: " + userId);
        } catch (NumberFormatException e) {
            Log.e(TAG, "Invalid user ID: " + e.getMessage(), e);
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

    }
}
