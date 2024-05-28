package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class CheckTrainingsActivity extends AppCompatActivity {

    private ImageView home, trainingRoutinesButton, profile, training_session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_trainings);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckTrainingsActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CheckTrainingsActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(CheckTrainingsActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(CheckTrainingsActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        String[] routineNames = {"superaniol", "aniolpeirna"};
        updateUIWithRoutines(routineNames);
    }

    private void updateUIWithRoutines(String[] routineNames) {
        LinearLayout routinesLayout = findViewById(R.id.trainingContainer);

        for (String name : routineNames) {
            View routineView = getLayoutInflater().inflate(R.layout.check_routine, routinesLayout, false);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView textView = routineView.findViewById(R.id.textViewRoutineName);

            textView.setText(name);

            routineView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(CheckTrainingsActivity.this, TrainingRoutinesActivity.class);
                    startActivity(intent);
                }
            });
            routinesLayout.addView(routineView);
        }
    }
}
