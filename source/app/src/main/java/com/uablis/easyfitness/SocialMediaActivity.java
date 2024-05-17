package com.uablis.easyfitness;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class SocialMediaActivity extends AppCompatActivity {

    ImageView homeButton, trainingRoutinesButton, trainingSessionButton, profileButton, back_arrow;
    private String[] exerciseNames = {"Usuario1", "Usuario2", "Usuario3"}; // Nombres de usuarios de ejemplo

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_social_media);

        back_arrow = findViewById(R.id.back_arrow);
        homeButton = findViewById(R.id.home);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        trainingSessionButton = findViewById(R.id.training_session);
        profileButton = findViewById(R.id.profile);

        // Llamar a la función para cargar usuarios en la interfaz de usuario
        updateUIWithUsers();

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
    }

    private void updateUIWithUsers() {
        LinearLayout usersContainer = findViewById(R.id.usersContainer);

        for (String name : exerciseNames) {
            // Inflate the user layout
            View userView = getLayoutInflater().inflate(R.layout.row_user_followers, usersContainer, false);
            TextView textView = userView.findViewById(R.id.userName);
            Button followButton = userView.findViewById(R.id.follow_button);

            // Set the user name in the TextView
            textView.setText(name);

            // Add the user view to the container
            usersContainer.addView(userView);
        }
    }
}