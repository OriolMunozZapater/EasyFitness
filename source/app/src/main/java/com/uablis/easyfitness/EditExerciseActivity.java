package com.uablis.easyfitness;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class EditExerciseActivity extends AppCompatActivity {

    private EditText exName;
    private EditText exDesc;
    private EditText exTipo;
    private RatingBar exExerciseRatingBar;
    private Button btnAddExercise;
    private Toolbar toolbar;
    private ImageView backArrow;
    private ImageView home, trainingRoutinesButton, profile, training_session;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_exercise);

        exName = findViewById(R.id.exName);
        exDesc = findViewById(R.id.exDesc);
        exTipo = findViewById(R.id.exTipo);
        exExerciseRatingBar = findViewById(R.id.exExerciseRating);
        btnAddExercise = findViewById(R.id.btnSaveExeriseEdit);
        profile = findViewById(R.id.profile);
        training_session = findViewById(R.id.training_session);
        trainingRoutinesButton = findViewById(R.id.training_routines);
        home = findViewById(R.id.home);

        toolbar = findViewById(R.id.toolbar);
        backArrow = findViewById(R.id.backArrow);

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditExerciseActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        training_session.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(EditExerciseActivity.this, TrainingLogActivity.class);
                startActivity(intent);
            }
        });

        home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de seguir usuarios
                Intent intent = new Intent(EditExerciseActivity.this, MainNetworkActivity.class);
                startActivity(intent);
            }
        });

        trainingRoutinesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Lógica para el botón de rutinas de entrenamiento
                Intent intent = new Intent(EditExerciseActivity.this, TrainingRoutinesActivity.class);
                startActivity(intent);
            }
        });

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextScreen();
            }
        });

        backArrow.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                backPressed();
            }
        });
    }
    public void nextScreen() {
        String exerciseName = exName.getText().toString();
        String exerciseDescription = exDesc.getText().toString();
        String exerciseType = exTipo.getText().toString();
        float exerciseRating = exExerciseRatingBar.getRating();

        finish();
    }

    public void backPressed() {
        finish();
    }
}