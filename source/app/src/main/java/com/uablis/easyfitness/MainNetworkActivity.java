package com.uablis.easyfitness;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

public class MainNetworkActivity extends AppCompatActivity {
    ImageView homeButton, trainingRoutinesButton, trainingSessionButton, profileButton;
    ImageButton botonSeguidos,botonSeguidores, botonConsultaRutinas, botonUsersSeguir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_network);

        homeButton = findViewById(R.id.home);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        trainingSessionButton = findViewById(R.id.training_session);
        profileButton = findViewById(R.id.profile);
        botonSeguidores = findViewById(R.id.botonSeguidores);
        botonSeguidos = findViewById(R.id.botonSeguidos);
        botonConsultaRutinas = findViewById(R.id.botonConsultaRutinas);
        botonUsersSeguir = findViewById(R.id.botonUsersSeguir);

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
