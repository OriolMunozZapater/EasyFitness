package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

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

    private ImageView home, training_routines, training, profile, profile_image;

    private Toolbar toolbar, appbar;

    private ImageButton edit_profile, btnPes;

    private TextView pesObjectiu, pesPropi, nomUser, sexe, altura;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        pesObjectiu = findViewById(R.id.pesObjectiu);
        pesPropi = findViewById(R.id.pesPropi);
        nomUser = findViewById(R.id.nomUser);
        altura = findViewById(R.id.altura);
        sexe = findViewById(R.id.sexe);



        profile = findViewById(R.id.profile);
        home = findViewById(R.id.home);
        training_routines = findViewById(R.id.training_routines);
        training = findViewById(R.id.training_session);
        btnPes = findViewById(R.id.btnEditPes);

        edit_profile = findViewById(R.id.btnEditPersonalData);

        getUserData();

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditData();
            }
        });

        btnPes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToEditWeight();
            }
        });

        training_routines.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ProfileActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void getUserData(){
        RequestQueue queue = Volley.newRequestQueue(this);
        String userId = UsuarioActual.getInstance().getUserId();

        String url = "http://172.17.176.1:8080/api/usuario/" + userId;

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            JSONObject jsonObject = jsonArray.getJSONObject(0);
                            String userName = jsonObject.getString("nombre");
                            String userSurname = jsonObject.getString("apellido");
                            String userEmail = jsonObject.getString("correo");
                            String userGender = jsonObject.getString("sexo");
                            String userActualWeight = jsonObject.getString("peso_actual");
                            String userHeight = jsonObject.getString("altura");
                            String userFoto = jsonObject.getString("foto");
                            String userDescription = jsonObject.getString("descripcion");
                            String userSocialMedia = jsonObject.getString("redes_sociales");
                            String userObjective = jsonObject.getString("objetivo");
                            String userFirstLogin = jsonObject.getString("firslLogin");



                            updateUIWithUserData(userActualWeight, userName, userGender, userHeight);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            Toast.makeText(ProfileActivity.this, "Error parsing JSON data", Toast.LENGTH_SHORT).show();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                Toast.makeText(ProfileActivity.this, "Error making API call: " + error.toString(), Toast.LENGTH_SHORT).show();
            }
        });

        queue.add(stringRequest);
    }

    private void updateUIWithUserData(String userName,String userActualWeight,
                                      String userHeight, String userGender) {
        // Actualiza las vistas con la información del usuario
        nomUser.setText(userName);
        sexe.setText(userGender);
        pesPropi.setText(userActualWeight);
        altura.setText(userHeight);

    }




    public void goToEditData() {
        Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
        startActivity(intent);
    }

    public void goToEditWeight() {
        Intent intent = new Intent(ProfileActivity.this, EditProfile.class);
        startActivity(intent);
    }

}
