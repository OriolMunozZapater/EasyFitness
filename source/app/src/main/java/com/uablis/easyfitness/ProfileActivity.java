package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ProfileActivity extends AppCompatActivity {

    private TextView pesObjectiu, pesPropi, nomUser, sexe, altura;
    private ImageButton edit_profile, btnPes;
    private ImageView imagePerfilUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        initializeViews();
        getUserData();
        setupListeners();
    }

    private void initializeViews() {
        pesObjectiu = findViewById(R.id.pesObjectiu);
        pesPropi = findViewById(R.id.pesPropi);
        nomUser = findViewById(R.id.nomUser);
        sexe = findViewById(R.id.sexe);
        altura = findViewById(R.id.altura);
        edit_profile = findViewById(R.id.btnEditPersonalData);
        btnPes = findViewById(R.id.btnEditPes);
        imagePerfilUser = findViewById(R.id.profile_image);
    }

    private void setupListeners() {
        edit_profile.setOnClickListener(v -> goToEditData());
        btnPes.setOnClickListener(v -> goToEditWeight());
        findViewById(R.id.training_routines).setOnClickListener(v -> startActivity(new Intent(ProfileActivity.this, TrainingRoutinesActivity.class)));
    }

    private void getUserData() {
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();
        String url = "http://172.17.176.1:8080/api/usuarios/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> handleResponse(response),
                error -> Toast.makeText(ProfileActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show());

        queue.add(stringRequest);
    }

    private void handleResponse(String response) {
        try {
            JSONObject jsonObject = new JSONObject(response);
            nomUser.setText(jsonObject.getString("nombre"));
            sexe.setText(jsonObject.getString("sexo"));
            pesPropi.setText(jsonObject.getString("peso_actual") + " Kg");
            altura.setText(jsonObject.getString("altura") + " cm");
            // You can set the target weight if it's included in the response
            // pesObjectiu.setText(jsonObject.getString("peso_objetivo") + " Kg");
        } catch (JSONException e) {
            Toast.makeText(ProfileActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
        }
    }

    public void goToEditData() {
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }

    public void goToEditWeight() {
        // This currently redirects to the same EditProfile activity, consider a specific weight editing activity or modal
        Intent intent = new Intent(this, EditProfile.class);
        startActivity(intent);
    }
}