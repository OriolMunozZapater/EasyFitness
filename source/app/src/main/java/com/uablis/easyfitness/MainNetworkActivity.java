package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainNetworkActivity extends AppCompatActivity {
    ImageButton botonSeguidos,botonSeguidores, botonConsultaRutinas, botonUsersSeguir;
    private ImageView home, trainingRoutinesButton, profile, training_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network);

        botonSeguidores = findViewById(R.id.botonSeguidores);
        botonSeguidos = findViewById(R.id.botonSeguidos);
        botonConsultaRutinas = findViewById(R.id.botonConsultaRutinas);
        botonUsersSeguir = findViewById(R.id.botonUsersSeguir);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainNetworkActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainNetworkActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(MainNetworkActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        botonSeguidores.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, FollowersUsersActivity.class);
                startActivity(intent);
            }
        });

        botonSeguidos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, FollowedUsersActivity.class);
                startActivity(intent);
            }
        });

        botonConsultaRutinas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de consultar rutinas
                Intent intent = new Intent(MainNetworkActivity.this, ViewRoutinesActivity.class);
                startActivity(intent);
            }
        });

        botonUsersSeguir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(MainNetworkActivity.this, DiscoverUsersActivity.class);
                startActivity(intent);
            }
        });

    }
}